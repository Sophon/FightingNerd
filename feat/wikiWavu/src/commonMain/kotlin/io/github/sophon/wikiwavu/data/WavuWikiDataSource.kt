package io.github.sophon.wikiwavu.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.core.wiki.usecase.DownloadMoveListUseCase
import io.github.sophon.wikiwavu.domain.BASE_URL
import io.github.sophon.wikiwavu.domain.CHAR_LIST_URL
import io.github.sophon.wikiwavu.domain.LIMIT_MOVES
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Technically, the source for the character list is not Wavu but Tekken Docs.
 * The appeals to make a table for Character have not been successful.
 */
internal interface WavuWikiDataSource {
    suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote>
    suspend fun downloadMoveList(
        table: String,
        characterData: DownloadMoveListUseCase.CharacterData
    ): Result<MoveListResponseDto, DataError.Remote>
}


internal class WavuWikiDataSourceImpl(
    private val httpClient: HttpClient,
): WavuWikiDataSource {
    override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
        return safeCall { httpClient.get(CHAR_LIST_URL) }
    }

    override suspend fun downloadMoveList(
        table: String,
        characterData: DownloadMoveListUseCase.CharacterData,
    ): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(BASE_URL) {
                parameter("action", "cargoquery")
                parameter("tables", table)
                parameter("where", "id LIKE '${characterData.name}%'")
                parameter("order_by", "id")
                parameter("format", "json")
                parameter("limit", LIMIT_MOVES)
                parameter("fields", "id,name,input,parent,target,damage,startup,recv,tot,crush,block,hit,ch,notes,alias,image,video,alt,_pageNamespace=ns")
            }
        }
    }
}