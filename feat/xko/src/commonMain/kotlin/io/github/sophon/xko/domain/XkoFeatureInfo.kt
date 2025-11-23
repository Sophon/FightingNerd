package io.github.sophon.xko.domain

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.xko.BuildKonfig
import io.github.sophon.xko.FEATURE_IMG_URL
import io.github.sophon.xko.FEATURE_NAME
import io.github.sophon.xko.FEATURE_URL

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