package io.github.sophon.wikimizuumi

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.mizuumi.BuildKonfig

object MizuumiFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.MBTL,
            Game.Uni2,
        ),
        version = BuildKonfig.VERSION,
    )
}