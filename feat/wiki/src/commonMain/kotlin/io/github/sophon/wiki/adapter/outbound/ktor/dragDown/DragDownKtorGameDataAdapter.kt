package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.adapter.outbound.ktor.KtorWikiAdapter
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * DragDown is separate - a character table, then one move query per character.
 * Image urls are built from the file name - no image requests.
 */
internal class DragDownKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : KtorWikiAdapter {

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val table = DragDownTables.characterTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
        val bucketQuery = "${table.toSelect()}.limit($LIMIT_CHARACTERS).run()"

        val characterListResult = safeCall<DragDownBucketResponseDto<DragDownCharacterResponseDto>> {
            httpClient.get(BASE_URL) {
                parameter("action", "bucket")
                parameter("query", bucketQuery)
                parameter("format", "json")
            }
        }
            .map { dto -> dto.bucket }
            .map { dtoList ->
                val imageUrlMap = dtoList
                    .map { dto -> dto.chara.formIconFileName() }
                    .toImageUrlMap()
                dtoList.toDomain(game, imageUrlMap)
            }
        return characterListResult
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val game = character.id.game
        val table = DragDownTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
        val bucketQuery = "${table.toSelect()}.where('chara','${character.remoteQueryId}').limit($LIMIT_MOVES).run()"

        val moveListResult = safeCall<DragDownBucketResponseDto<DragDownMoveResponseDto>> {
            httpClient.get(BASE_URL) {
                parameter("action", "bucket")
                parameter("query", bucketQuery)
                parameter("format", "json")
            }
        }
            .map { dto -> dto.bucket }
            .map { dtoList ->
                val imageUrlMap = dtoList
                    .flatMap { dto -> dto.image.orEmpty() + dto.hitbox.orEmpty() }
                    .map { fileName -> fileName.trim() }
                    .filter { fileName -> fileName.isNotEmpty() }
                    .toImageUrlMap()
                dtoList.toDomain(character, imageUrlMap)
            }
        return moveListResult
    }

    override fun normalizeMoveInput(game: Game, input: String): String {
        return input
    }

    private fun List<String>.toImageUrlMap(): Map<String, String> {
        val imageUrlMap = distinct().associateWith { fileName -> "$IMAGE_URL/$fileName" }
        return imageUrlMap
    }


    private companion object {
        const val BASE_URL = "https://dragdown.wiki/w/api.php"
        const val IMAGE_URL = "https://dragdown.wiki/wiki/Special:Redirect/file"
        const val LIMIT_CHARACTERS = 50
        const val LIMIT_MOVES = 500
    }
}
