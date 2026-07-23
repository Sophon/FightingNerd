package io.github.sophon.wikidustloop.integration

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.dustloop.BuildKonfig
import io.github.sophon.wikidustloop.domain.FEATURE_IMG_URL
import io.github.sophon.wikidustloop.domain.FEATURE_NAME
import io.github.sophon.wikidustloop.domain.FEATURE_URL

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
            Game.MTFS,
        ),
        version = BuildKonfig.VERSION,
    )
}