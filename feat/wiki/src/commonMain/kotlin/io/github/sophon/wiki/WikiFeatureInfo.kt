package io.github.sophon.wiki

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.wiki.app.FEATURE_IMG_URL
import io.github.sophon.wiki.app.FEATURE_NAME
import io.github.sophon.wiki.app.FEATURE_URL

object WikiFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        version = BuildKonfig.VERSION,
    )
}
