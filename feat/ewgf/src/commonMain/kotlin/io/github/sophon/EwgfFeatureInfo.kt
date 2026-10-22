package io.github.sophon

import io.github.sophon.app.FEATURE_NAME
import io.github.sophon.app.FEATURE_URL
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.ewgf.BuildKonfig

object EwgfFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        version = BuildKonfig.VERSION,
        iconUrl = "https://i.imgur.com/Fzu2phV.png"
    )
}
