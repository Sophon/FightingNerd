package io.github.sophon.wikimizuumi.integration

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.mizuumi.BuildKonfig
import io.github.sophon.wikimizuumi.domain.FEATURE_IMG_URL
import io.github.sophon.wikimizuumi.domain.FEATURE_NAME
import io.github.sophon.wikimizuumi.domain.FEATURE_URL

object MizuumiFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.MBTL,
            Game.Uni2,
            Game.VSAV,
        ),
        version = BuildKonfig.VERSION,
    )
}