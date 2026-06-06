package io.github.sophon.xko.integration

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.xko.BuildKonfig
import io.github.sophon.xko.domain.FEATURE_IMG_URL
import io.github.sophon.xko.domain.FEATURE_NAME
import io.github.sophon.xko.domain.FEATURE_URL

object XkoFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.Xko,
        ),
        version = BuildKonfig.VERSION,
    )
}