package io.github.sophon.domain

import io.github.sophon.admin.BuildKonfig
import io.github.sophon.core.feature.FeatureInfo

object AdminFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        version = BuildKonfig.VERSION,
    )
}