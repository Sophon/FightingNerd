package io.github.sophon.domain

import io.github.sophon.FEATURE_NAME
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.stats.BuildKonfig

object StatsFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_NAME,
        version = BuildKonfig.VERSION,
    )
}
