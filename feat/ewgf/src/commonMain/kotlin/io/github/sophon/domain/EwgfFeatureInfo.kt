package io.github.sophon.domain

import io.github.sophon.FEATURE_NAME
import io.github.sophon.FEATURE_URL
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.ewgf.BuildKonfig

object EwgfFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        version = BuildKonfig.VERSION,
    )
}