package io.github.sophon.glossaryinfil

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.glossaryinfil.app.FEATURE_NAME
import io.github.sophon.glossaryinfil.app.FEATURE_URL

object GlossaryFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        version = BuildKonfig.VERSION,
        iconUrl = "https://i.imgur.com/OigKJBY.png",
    )
}
