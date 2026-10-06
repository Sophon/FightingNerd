package io.github.sophon.wiki.adapter.outbound.ktor.dragDown

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * DragDown is separate - a character table, then one move query per character.
 * Cargo export returns plain JSON arrays, and image urls are built from the file name - no image requests.
 */
internal class DragDownKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {

    override fun fetch(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = flow {
            fetchCharacterList(game)
                .onSuccess { characterList ->
                    for (character in characterList) {
                        val characterWithMovesResult = fetchMoveList(game, character)
                            .map { moveList -> character to moveList }
                        emit(characterWithMovesResult)
                    }
                }
                .onError { error -> emit(Result.Error(error)) }
        }
        return flow
    }

    private suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val table = DragDownTables.characterTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<List<DragDownCharacterResponseDto>> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("format", "json")
                parameter("limit", LIMIT_CHARACTERS)
            }
        }
            .map { dtoList ->
                val imageUrlMap = dtoList
                    .map { dto -> dto.chara.formIconFileName() }
                    .toImageUrlMap()
                dtoList.toDomain(game, imageUrlMap)
            }
        return characterListResult
    }

    private suspend fun fetchMoveList(
        game: Game,
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val table = DragDownTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<List<DragDownMoveResponseDto>> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("where", "chara=\"${character.remoteQueryId}\"")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
            }
        }
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

    private fun List<String>.toImageUrlMap(): Map<String, String> {
        val imageUrlMap = distinct().associateWith { fileName -> "$IMAGE_URL/$fileName" }
        return imageUrlMap
    }


    private companion object {
        const val BASE_URL = "https://dragdown.wiki/wiki/Special:CargoExport"
        const val IMAGE_URL = "https://dragdown.wiki/wiki/Special:Redirect/file"
        const val LIMIT_CHARACTERS = 50
        const val LIMIT_MOVES = 500
    }
}
