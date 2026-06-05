package io.github.sophon.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.data.dto.BattlesDto
import io.github.sophon.domain.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

internal interface EwgfDataSource {
    suspend fun getBattles(polarisId: String): Result<BattlesDto, DataError.Remote>
}


internal class EwgfDataSourceImpl(
    private val apiToken: String,
    private val httpClient: HttpClient,
): EwgfDataSource {
    override suspend fun getBattles(
        polarisId: String,
    ): Result<BattlesDto, DataError.Remote> {
        return safeCall {
            httpClient.get("${BASE_URL}/battles/$polarisId") {
                header(HttpHeaders.Authorization, "Bearer $apiToken")
            }
        }
    }
}
