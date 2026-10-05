package io.github.sophon

import io.github.sophon.admin.BuildKonfig
import io.github.sophon.app.domain.FEATURE_NAME
import io.github.sophon.app.domain.FEATURE_URL
import io.github.sophon.core.featureConfig.model.FeatureInfo

object AdminFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        version = BuildKonfig.VERSION,
    )
}