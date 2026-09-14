package io.github.sophon.fightingnerd.feat.changelog.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.fightingnerd.feat.changelog.URL_RELEASE_CHANGELOG
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal interface ChangelogRemoteSource {
    suspend fun getReleaseNotes(): Result<List<ReleaseDto>, DataError.Remote>
}


internal class ChangelogRemoteSourceImpl(
    private val httpClient: HttpClient,
) : ChangelogRemoteSource {
    override suspend fun getReleaseNotes(): Result<List<ReleaseDto>, DataError.Remote> {
        return safeCall<List<ReleaseDto>> { httpClient.get(URL_RELEASE_CHANGELOG) }
    }
}
