package io.github.sophon.wikiwavu.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.wikiwavu.CHAR_LIST_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal interface TekkenDocsDataSource {
    suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote>
}

internal class TekkenDocsDataSourceImpl(
    private val httpClient: HttpClient,
): TekkenDocsDataSource {
    override suspend fun downloadCharacterList(): Result<CharacterListResponseDto, DataError.Remote> {
        return safeCall { httpClient.get(CHAR_LIST_URL) }
    }
}