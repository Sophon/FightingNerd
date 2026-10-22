package io.github.sophon

import io.github.sophon.app.FEATURE_NAME
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.stats.BuildKonfig

object StatsFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_NAME,
        version = BuildKonfig.VERSION,
    )
}