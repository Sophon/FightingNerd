package io.github.sophon.wiki.adapter.outbound.ktor.dustLoop

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.adapter.outbound.ktor.getWikiImageUrl
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class DustLoopKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val table = DustLoopTables.characterTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<DustLoopCharacterListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("format", "json")
                parameter("limit", LIMIT_CHARACTERS)
            }
        }
            .flatMap { dto ->
                resolveCharacterImageUrls(dto)
                    .map { imageUrlMap -> dto.toDomain(game, imageUrlMap) }
            }
        return characterListResult
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val game = character.id.game
        val table = DustLoopTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<DustLoopMoveListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("where", "chara=\"${character.remoteQueryId}\"")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
            }
        }
            .flatMap { dto ->
                resolveHitboxUrls(dto)
                    .map { imageUrlMap -> dto.toDomain(game, character, imageUrlMap) }
            }
        return moveListResult
    }

    override suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }

    private suspend fun resolveCharacterImageUrls(
        dto: DustLoopCharacterListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoQuery
            .flatMap { query -> listOfNotNull(query.title.icon, query.title.portrait) }
            .distinct()

        val imageUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameList,
        )
        return imageUrlResult
    }

    private suspend fun resolveHitboxUrls(
        dto: DustLoopMoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoQuery
            .asSequence()
            .flatMap { query -> listOfNotNull(query.title.hitboxes, query.title.images) }
            .flatMap { fileNames -> fileNames.split(";", "\\") }
            .map { fileName -> fileName.trim() }
            .filter { fileName -> fileName.isNotEmpty() }
            .distinct()
            .toList()

        val hitboxUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameList,
        )
        return hitboxUrlResult
    }


    private companion object {
        const val BASE_URL = "https://www.dustloop.com/wiki/api.php"
        const val LIMIT_CHARACTERS = 50
        const val LIMIT_MOVES = 500
    }
}
