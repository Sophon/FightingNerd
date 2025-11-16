package io.github.sophon.glossaryinfil.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.glossaryinfil.BuildKonfig
import io.github.sophon.glossaryinfil.FEATURE_IMG_URL
import io.github.sophon.glossaryinfil.FEATURE_NAME
import io.github.sophon.glossaryinfil.FEATURE_URL

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