package com.example.glossaryinfil.data

import com.example.glossaryinfil.BASE_URL
import com.example.core.domain.DataError
import com.example.core.domain.Result
import com.example.core.network.safeCall
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