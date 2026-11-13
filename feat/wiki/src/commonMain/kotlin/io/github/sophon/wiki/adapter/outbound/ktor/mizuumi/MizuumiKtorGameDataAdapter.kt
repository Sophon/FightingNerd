package io.github.sophon.wiki.adapter.outbound.ktor.mizuumi

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.adapter.outbound.ktor.KtorWikiAdapter
import io.github.sophon.wiki.adapter.outbound.ktor.getWikiImageUrl
import io.github.sophon.wiki.adapter.outbound.ktor.normalize2dInputs
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class MizuumiKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : KtorWikiAdapter {
    private val gitHubCharacterListUrlByGame = mapOf(
        Game.MBTL to URL_GITHUB_MBTL_CHAR_LIST,
        Game.VSAV to URL_GITHUB_VSAV_CHAR_LIST,
    )

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val gitHubCharacterList = gitHubCharacterListUrlByGame[game]
        val characterListResult = if (gitHubCharacterList == null) {
            fetchFromWiki(game)
        } else {
            fetchFromGitHub(game, gitHubCharacterList)
        }
        return characterListResult
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val game = character.id.game
        val table = MizuumiTables.moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<MizuumiMoveListResponseDto> {
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
                    .map { hitboxUrlMap -> dto.toDomain(game, character, hitboxUrlMap) }
            }
        return moveListResult
    }

    override fun normalizeMoveInput(game: Game, input: String): String {
        val normalized = input.normalize2dInputs()
        return normalized
    }

    private suspend fun fetchFromGitHub(
        game: Game,
        url: String,
    ): Result<List<Character>, DataError.Remote> {
        val characterListResult = safeCall<MizuumiCharacterJsonResponseDto> {
            httpClient.get(url)
        }
            .map { dto -> dto.toDomain(game) }
        return characterListResult
    }

    private suspend fun fetchFromWiki(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val table = MizuumiTables.characterTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<MizuumiCharacterListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table.name)
                parameter("fields", table.fieldList.joinToString(","))
                parameter("format", "json")
                parameter("limit", LIMIT_CHARACTERS)
            }
        }
            .flatMap { dto ->
                val charaList = dto.cargoquery.map { characterTitle -> characterTitle.title.chara }
                resolveCharacterIconUrls(game, charaList)
                    .map { iconUrlMap -> dto.toDomain(game, iconUrlMap) }
            }
        return characterListResult
    }

    private suspend fun resolveHitboxUrls(
        dto: MizuumiMoveListResponseDto,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameList = dto.cargoquery
            .asSequence()
            .flatMap { moveTitle -> listOfNotNull(moveTitle.title.hitboxes, moveTitle.title.images) }
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

    /**
     * Returns icon URLs keyed the way the character mappers look them up.
     */
    private suspend fun resolveCharacterIconUrls(
        game: Game,
        charaList: List<String>,
    ): Result<Map<String, String>, DataError.Remote> {
        val fileNameByKey = charaList.distinct().toIconFileNameByKey(game)

        val iconUrlResult = getWikiImageUrl(
            httpClient = httpClient,
            url = BASE_URL,
            fileNames = fileNameByKey.values.distinct(),
        )
            .map { urlByFileName ->
                fileNameByKey
                    .mapNotNull { (key, fileName) -> urlByFileName[fileName]?.let { url -> key to url } }
                    .toMap()
            }
        return iconUrlResult
    }

    private fun List<String>.toIconFileNameByKey(game: Game): Map<String, String> {
        val fileNameByKey = when (game) {
            Game.Uni2 -> this.associateWith { chara -> "UNI2_${chara}_CSel.png" }

            else -> emptyMap()
        }
        return fileNameByKey
    }


    private companion object {
        const val BASE_URL = "https://mizuumi.wiki/api.php"
        const val LIMIT_CHARACTERS = 50
        const val LIMIT_MOVES = 500
        const val URL_GITHUB_MBTL_CHAR_LIST = "https://raw.githubusercontent.com/Sophon/FightingNerd/refs/heads/dev/res/characters/mbtl-characters.json"
        const val URL_GITHUB_VSAV_CHAR_LIST = "https://raw.githubusercontent.com/Sophon/FightingNerd/refs/heads/dev/res/characters/vsav-characters.json"
    }
}
