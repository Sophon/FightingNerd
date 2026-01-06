package io.github.sophon.wikidustloop.domain

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.dustloop.BuildKonfig
import io.github.sophon.wikidustloop.FEATURE_IMG_URL
import io.github.sophon.wikidustloop.FEATURE_NAME
import io.github.sophon.wikidustloop.FEATURE_URL

object DustLoopFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.GGST,
            Game.DBFZ,
            Game.GBVSR,
            Game.BBCF,
        ),
        version = BuildKonfig.VERSION,
    )
}