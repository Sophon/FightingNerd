package com.example.glossaryinfil.data

import com.example.glossaryinfil.BASE_URL
import io.github.sophon.core.domain.DataError
import io.github.sophon.core.domain.Result
import io.github.sophon.core.network.safeCall
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