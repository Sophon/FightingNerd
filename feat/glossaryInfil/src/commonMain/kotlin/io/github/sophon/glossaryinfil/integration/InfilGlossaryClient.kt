package io.github.sophon.glossaryinfil.integration

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.glossaryinfil.integration.model.GlossaryError
import io.github.sophon.glossaryinfil.integration.model.GlossaryItem

interface InfilGlossaryClient {
    fun getFeatureInfo(): FeatureInfo

    suspend fun downloadGlossary(): EmptyResult<GlossaryError>
    suspend fun search(query: String): Result<List<GlossaryItem>, GlossaryError>
}
