package io.github.sophon.integration

import io.github.sophon.admin.BuildKonfig
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.domain.FEATURE_NAME
import io.github.sophon.domain.FEATURE_URL

object AdminFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        version = BuildKonfig.VERSION,
    )
}