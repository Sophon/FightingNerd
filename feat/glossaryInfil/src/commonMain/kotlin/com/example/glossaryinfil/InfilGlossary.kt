package com.example.glossaryinfil

import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import com.example.core.domain.Service
import com.example.core.domain.Source
import com.example.glossaryinfil.domain.GlossaryItem
import com.example.glossaryinfil.usecase.CacheGlossaryUseCase
import com.example.glossaryinfil.usecase.DownloadGlossaryUseCase
import com.example.glossaryinfil.usecase.FetchDataForTermUseCase
import io.github.aakira.napier.Napier

interface InfilGlossary: Service {
    suspend fun downloadGlossary(): EmptyResult<GlossaryError>
    suspend fun search(query: String): Result<List<GlossaryItem>, GlossaryError>
}

internal class InfilGlossaryImpl(
    private val downloadGlossaryUseCase: DownloadGlossaryUseCase,
    private val cacheGlossaryUseCase: CacheGlossaryUseCase,
    private val fetchDataForTermUseCase: FetchDataForTermUseCase,
): InfilGlossary {

    override suspend fun downloadGlossary(): EmptyResult<GlossaryError> {
        return when (val result = downloadGlossaryUseCase.invoke()) {
            is Result.Success -> {
                cacheGlossaryUseCase.invoke(result.data)
                Napier.d(tag = TAG) { "Successfully retrieved glossary; ${result.data.size} keys" }
                Result.Success(Unit)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { result.error.toString() }
                Result.Error(result.error)
            }
        }
    }

    override suspend fun search(query: String): Result<List<GlossaryItem>, GlossaryError> {
        Napier.d(tag = TAG) { "Query: $query" }
        return fetchDataForTermUseCase.invoke(query)
    }

    override fun source(): Source {
        return Source(
            name = SERVICE_NAME,
            iconUrl = "https://i.imgur.com/OigKJBY.png"
        )
    }
}


private const val TAG = "com.example.glossaryinfil.InfilGlossary"