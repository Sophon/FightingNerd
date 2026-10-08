package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.wiki.app.outPort.FetchGameDataPort
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class WavuKtorGameDataAdapter(
    private val httpClient: HttpClient,
) : FetchGameDataPort {
    private val characterListUrlByGame = mapOf(
        Game.Tekken8 to URL_CHAR_LIST,
    )
    private val moveTableByGame = mapOf(
        Game.Tekken8 to TABLE_T8_MOVE_LIST,
    )

    override suspend fun fetchCharacterList(
        game: Game,
    ): Result<List<Character>, DataError.Remote> {
        val url = characterListUrlByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val characterListResult = safeCall<WavuCharacterListResponseDto> {
            httpClient.get(url)
        }
            .map { dto -> dto.toDomain(game) }
        return characterListResult
    }

    override suspend fun fetchMoveList(
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val game = character.id.game
        val table = moveTableByGame[game]
            ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<WavuMoveListResponseDto> {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("where", "id LIKE '${character.remoteQueryId}%'")
                parameter("order_by", "id")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
                parameter("fields", "id,name,input,parent,target,damage,startup,recv,tot,crush,block,hit,ch,notes,alias,image,video,alt,_pageNamespace=ns")
            }
        }.map { dto -> dto.toDomain(character) }

        return moveListResult
    }

    override suspend fun fetchGameData(
        game: Game,
    ): Result<List<Pair<Character, List<Move>>>, DataError.Remote> {
        return Result.Error(DataError.Remote.PAGE_NOT_FOUND)
    }


    private companion object {
        const val TABLE_T8_MOVE_LIST = "Move"
        const val BASE_URL = "https://wavu.wiki/w/api.php"
        const val LIMIT_MOVES = 500
        const val URL_CHAR_LIST = "https://raw.githubusercontent.com/Sophon/FightingNerd/refs/heads/dev/res/t8-characters.json"
    }
}
