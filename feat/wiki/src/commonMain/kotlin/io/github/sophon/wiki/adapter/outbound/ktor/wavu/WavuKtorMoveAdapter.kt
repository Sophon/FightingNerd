package io.github.sophon.wiki.adapter.outbound.ktor.wavu

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.port.outbound.FetchMoveListPort
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class WavuKtorMoveAdapter(
    private val httpClient: HttpClient,
) : FetchMoveListPort {
    override suspend fun fetch(
        game: Game,
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        val table = moveTableByGame[game] ?: return Result.Error(DataError.Remote.PAGE_NOT_FOUND)

        val moveListResult = safeCall<MoveListResponseDto> {
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
}


private val moveTableByGame = mapOf(
    Game.Tekken8 to TABLE_T8_MOVE_LIST,
)

private const val TABLE_T8_MOVE_LIST = "Move"
