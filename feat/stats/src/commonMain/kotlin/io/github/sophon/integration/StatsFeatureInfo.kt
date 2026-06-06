package io.github.sophon.integration

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.domain.FEATURE_NAME
import io.github.sophon.stats.BuildKonfig

object StatsFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_NAME,
        version = BuildKonfig.VERSION,
    )
}