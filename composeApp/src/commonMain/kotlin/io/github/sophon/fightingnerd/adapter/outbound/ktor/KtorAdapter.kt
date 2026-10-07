package io.github.sophon.fightingnerd.adapter.outbound.ktor

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.network.safeCall
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Release
import io.github.sophon.fightingnerd.app.outPort.ReleasePort
import io.ktor.client.HttpClient
import io.ktor.client.request.get

@ExcludeFromCoverage("TODO: needs a fake HTTP engine")
internal class KtorAdapter(
    private val httpClient: HttpClient,
): ReleasePort {
    override suspend fun getReleases(): Result<List<Release>, AppError> {
        val result = safeCall<List<ReleaseDto>> { httpClient.get(URL_RELEASES) }
            .map { releaseDtoList -> releaseDtoList.toDomain() }
            .mapError { error -> AppError.IOError(error.name) }
        return result
    }
}


private const val URL_RELEASES = "https://api.github.com/repos/Sophon/FightingNerd/releases"
