package io.github.sophon.glossaryinfil

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.glossaryinfil.domain.GlossaryItem
import io.github.sophon.glossaryinfil.usecase.CacheGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.DownloadGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.FetchDataForTermUseCase
import io.github.aakira.napier.Napier

interface InfilGlossary {
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
}


private const val TAG = "io.github.sophon.glossaryinfil.InfilGlossary"