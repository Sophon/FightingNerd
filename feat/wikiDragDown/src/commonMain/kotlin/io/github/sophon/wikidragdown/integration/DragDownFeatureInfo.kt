package io.github.sophon.wikidragdown.integration

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wikidragdown.BuildKonfig
import io.github.sophon.wikidragdown.domain.FEATURE_IMG_URL
import io.github.sophon.wikidragdown.domain.FEATURE_NAME
import io.github.sophon.wikidragdown.domain.FEATURE_URL

object DragDownFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.ROA2,
        ),
        version = BuildKonfig.VERSION,
    )
}