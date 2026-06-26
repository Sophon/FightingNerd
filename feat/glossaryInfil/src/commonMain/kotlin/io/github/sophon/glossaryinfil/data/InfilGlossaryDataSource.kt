package io.github.sophon.glossaryinfil.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.network.safeCall
import io.github.sophon.glossaryinfil.domain.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get

internal interface InfilGlossaryDataSource {
    suspend fun getGlossary(): Result<List<GlossaryItemDto>, DataError.Remote>
}

internal class InfilGlossaryDataSourceImpl(
    private val httpClient: HttpClient
): InfilGlossaryDataSource {
    override suspend fun getGlossary(): Result<List<GlossaryItemDto>, DataError.Remote> {
        return safeCall { httpClient.get(BASE_URL) }
    }
}