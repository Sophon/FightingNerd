package io.github.sophon.xko.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.xko.domain.URL_BASE
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface XkoWikiDataSource {
    suspend fun downloadMoveList(): Result<MoveListResponseDto, DataError.Remote>
}

internal class XkoWikiDataSourceImpl(
    private val httpClient: HttpClient,
): XkoWikiDataSource {
    override suspend fun downloadMoveList(): Result<MoveListResponseDto, DataError.Remote> {
        return safeCall {
            httpClient.get(URL_BASE) {
                parameter("action", "bucket")
                parameter("query", "bucket('move').select('page_name', 'input', 'damage', 'guard', 'startup', 'active', 'recovery', 'onblock', 'cancel', 'invuln').limit(5000).run()")
                parameter("format", "json")
            }
        }
    }
}
