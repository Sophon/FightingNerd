package io.github.sophon.wikiwavu.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.wikiwavu.BASE_URL
import io.github.sophon.wikiwavu.CHAR_LIST_URL
import io.github.sophon.wikiwavu.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Technically, the source for the character list is not Wavu but Tekken Docs.
 * The appeals to make a table for Character have not been successful.
 */

internal class WavuWikiDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
        return safeCall { httpClient.get(CHAR_LIST_URL) }
    }

    suspend fun downloadMoveListFor(
        charName: String
    ): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", "Move")
                parameter("where", "id LIKE '$charName%'")
                parameter("order_by", "id")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
                parameter("fields", "id,name,input,parent,target,damage,startup,recv,tot,crush,block,hit,ch,notes,alias,image,video,alt,_pageNamespace=ns")
            }
        }
    }
}