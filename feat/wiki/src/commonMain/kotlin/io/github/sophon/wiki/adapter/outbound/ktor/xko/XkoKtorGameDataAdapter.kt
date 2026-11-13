package io.github.sophon.wiki.adapter.outbound.ktor.xko

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

internal class XkoKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : KtorWikiAdapter {
    private val gitHubCharacterListUrlByGame = mapOf(
        Game.Xko to URL_GITHUB_XKO_CHAR_LIST,
    )

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val url = gitHubCharacterListUrlByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<XkoCharacterJsonResponseDto> {
            httpClient.get(url)
        }
            .map { dto -> dto.toDomain(game) }
        return characterListResult
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val moveSelect = moveSelectByGame[character.id.game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
        val bucketQuery = "$moveSelect.where('page_name', '${character.remoteQueryId}').limit($LIMIT_MOVES).run()"

        val moveListResult = safeCall<XkoMoveListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "bucket")
                parameter("query", bucketQuery)
                parameter("format", "json")
            }
        }
            .map { dto -> dto.toDomain() }
        return moveListResult
    }

    override suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        val moveSelect = moveSelectByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
        val bucketQuery = "$moveSelect.limit($LIMIT_MOVES).run()"

        val gameDataResult = safeCall<XkoMoveListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "bucket")
                parameter("query", bucketQuery)
                parameter("format", "json")
            }
        }
            .map { dto -> dto.toDomainAll(game) }
        return gameDataResult
    }

    override fun normalizeMoveInput(game: Game, input: String): String {
        val normalized = input.normalizeXkoInput()
        return normalized
    }


    private companion object {
        const val BASE_URL = "https://wiki.play2xko.com/en-us/api.php"
        const val LIMIT_MOVES = 5000
        const val URL_GITHUB_XKO_CHAR_LIST = "https://raw.githubusercontent.com/Sophon/FightingNerd/refs/heads/dev/res/characters/xko-characters.json"

        val moveSelectByGame = mapOf(
            Game.Xko to "bucket('move').select('page_name', 'input', 'damage', 'guard', 'startup', 'active', 'recovery', 'onblock', 'cancel', 'invuln')",
        )
    }
}
