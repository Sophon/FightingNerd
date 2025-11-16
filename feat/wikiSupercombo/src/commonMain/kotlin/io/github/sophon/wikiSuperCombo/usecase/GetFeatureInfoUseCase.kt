package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.ExcludeFromCoverage
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.wikiSuperCombo.BuildKonfig
import io.github.sophon.wikiSuperCombo.FEATURE_IMG_URL
import io.github.sophon.wikiSuperCombo.FEATURE_NAME
import io.github.sophon.wikiSuperCombo.FEATURE_URL

@ExcludeFromCoverage("Feature Info static set")
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