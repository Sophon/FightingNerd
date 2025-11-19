package io.github.sophon.xko.data

import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.ktor.client.HttpClient

internal interface XkoWikiDataSource {
    suspend fun downloadMoveList(): Result<MoveListResponseDto, DataError.Remote>
}

internal class XkoWikiDataSourceImpl(
    private val httpClient: HttpClient,
): XkoWikiDataSource {
    override suspend fun downloadMoveList(): Result<MoveListResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }
}