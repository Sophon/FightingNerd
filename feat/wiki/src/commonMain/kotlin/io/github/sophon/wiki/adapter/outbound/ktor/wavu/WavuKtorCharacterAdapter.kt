package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wiki.application.port.outbound.FetchCharacterListPort
import io.ktor.client.HttpClient
import io.ktor.client.request.get

/**
 * Technically, the source for the character list is not Wavu but our GitHub json file.
 */
internal class WavuKtorCharacterAdapter(
    private val httpClient: HttpClient,
) : FetchCharacterListPort {
    override suspend fun fetch(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val url = characterListUrlByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<CharacterListResponseDto> {
            httpClient.get(url)
        }
            .map { dto -> dto.toDomain() }
        return characterListResult
    }
}


private val characterListUrlByGame = mapOf(
    Game.Tekken8 to CHAR_LIST_URL,
)
