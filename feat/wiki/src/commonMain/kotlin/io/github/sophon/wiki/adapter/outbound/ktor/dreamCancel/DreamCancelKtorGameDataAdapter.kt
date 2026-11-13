package io.github.sophon.wiki.adapter.outbound.ktor.dreamCancel

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.adapter.outbound.ktor.KtorWikiAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.getWikiImageUrl
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class DreamCancelKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : KtorWikiAdapter {
    private val gitHubCharacterListUrlByGame = mapOf(
        Game.KoFXV to URL_GITHUB_KOFXV_CHAR_LIST,
        Game.COTW to URL_GITHUB_COTW_CHAR_LIST,
    )

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val url = gitHubCharacterListUrlByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<DreamCancelCharacterJsonResponseDto> {
            httpClient.get(url)
        }
            .map { dto -> dto.toDomain(game) }
        return characterListResult
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val game = character.id.game
        val table = DreamCancelTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<DreamCancelMoveListResponseDto> {
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
                    .map { hitboxUrlMap -> dto.toDomain(game, hitboxUrlMap) }
            }
        return moveListResult
    }

    override fun normalizeMoveInput(game: Game, input: String): String {
        val normalized = input.normalizeDreamCancelInput()
        return normalized
    }

    private suspend fun resolveHitboxUrls(
        dto: DreamCancelMoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoQuery
            .asSequence()
            .flatMap { query -> listOfNotNull(query.title.hitboxes, query.title.images) }
            .flatMap { fileNames -> fileNames.split(",") }
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
        const val BASE_URL = "https://dreamcancel.com/w/api.php"
        const val LIMIT_MOVES = 500
        const val URL_GITHUB_KOFXV_CHAR_LIST = "https://raw.githubusercontent.com/Sophon/FightingNerd/refs/heads/dev/res/characters/kofxv-characters.json"
        const val URL_GITHUB_COTW_CHAR_LIST = "https://raw.githubusercontent.com/Sophon/FightingNerd/refs/heads/dev/res/characters/cotw-characters.json"
    }
}
