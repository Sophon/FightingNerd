package io.github.sophon.wiki.adapter.outbound.ktor.xko

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class XkoKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {

    override fun fetch(
        game: Game,
    ): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>> {
        val flow = flow {
            fetchGameData(game)
                .onSuccess { gameData ->
                    for (characterWithMoves in gameData) {
                        emit(Result.Success(characterWithMoves))
                    }
                }
                .onError { error -> emit(Result.Error(error)) }
        }
        return flow
    }

    private suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        val bucketQuery = bucketQueryByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

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


    private companion object {
        const val BASE_URL = "https://wiki.play2xko.com/en-us/api.php"

        val bucketQueryByGame = mapOf(
            Game.Xko to "bucket('move').select('page_name', 'input', 'damage', 'guard', 'startup', 'active', 'recovery', 'onblock', 'cancel', 'invuln').limit(5000).run()",
        )
    }
}
