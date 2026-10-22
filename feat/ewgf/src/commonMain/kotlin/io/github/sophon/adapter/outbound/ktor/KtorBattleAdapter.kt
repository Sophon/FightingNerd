package io.github.sophon.adapter.outbound.ktor

import io.github.sophon.app.outPort.FetchBattleListPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.network.safeCall
import io.github.sophon.model.Battle
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

internal class KtorBattleAdapter(
    private val apiToken: String,
    private val httpClient: HttpClient,
): FetchBattleListPort {
    override suspend fun fetch(polarisId: String): Result<List<Battle>, DataError.Remote> {
        val battleListResult = safeCall<BattleListResponseDto> {
            httpClient.get("$BASE_URL/battles/$polarisId") {
                header(HttpHeaders.Authorization, "Bearer $apiToken")
            }
        }
            .map { dto -> dto.toDomain(polarisId) }
        return battleListResult
    }


    private companion object {
        const val BASE_URL = "https://api.ewgf.gg/external"
    }
}
