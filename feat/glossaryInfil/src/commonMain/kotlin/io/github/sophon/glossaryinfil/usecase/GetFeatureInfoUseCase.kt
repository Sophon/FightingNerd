package io.github.sophon.glossaryinfil.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.glossaryinfil.BuildKonfig
import io.github.sophon.glossaryinfil.domain.FEATURE_IMG_URL
import io.github.sophon.glossaryinfil.domain.FEATURE_NAME
import io.github.sophon.glossaryinfil.domain.FEATURE_URL

@ExcludeFromCoverage("Feature info static set")
internal class GetFeatureInfoUseCase {
    fun invoke(): FeatureInfo {
        return FeatureInfo(
            name = FEATURE_NAME,
            url = FEATURE_URL,
            iconUrl = FEATURE_IMG_URL,
            version = BuildKonfig.VERSION,
        )
    }
}