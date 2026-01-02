package io.github.sophon.glossaryinfil

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.glossaryinfil.domain.GlossaryItem
import io.github.sophon.glossaryinfil.usecase.CacheGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.DownloadGlossaryUseCase
import io.github.sophon.glossaryinfil.usecase.FetchDataForTermUseCase
import io.github.sophon.glossaryinfil.usecase.GetFeatureInfoUseCase

interface InfilGlossaryClient {
    fun getFeatureInfo(): FeatureInfo

    suspend fun downloadGlossary(): EmptyResult<GlossaryError>
    suspend fun search(query: String): Result<List<GlossaryItem>, GlossaryError>
}

internal class InfilGlossaryClientImpl(
    private val getFeatureInfoUseCase: GetFeatureInfoUseCase,
    private val downloadGlossaryUseCase: DownloadGlossaryUseCase,
    private val cacheGlossaryUseCase: CacheGlossaryUseCase,
    private val fetchDataForTermUseCase: FetchDataForTermUseCase,
): InfilGlossaryClient {
    override fun getFeatureInfo(): FeatureInfo {
        return getFeatureInfoUseCase.invoke()
    }

    override suspend fun downloadGlossary(): EmptyResult<GlossaryError> {
        return when (val result = downloadGlossaryUseCase.invoke()) {
            is Result.Success -> {
                cacheGlossaryUseCase.invoke(result.data)
                Napier.i(tag = TAG) { "Successfully retrieved glossary; ${result.data.size} keys" }
                Result.Success(Unit)
            }
            is Result.Error -> {
                Napier.e(tag = TAG) { "downloadGlossary: ${result.error}" }
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