package io.github.sophon.dreamcancel.integration

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.dreamcancel.domain.FEATURE_IMG_URL
import io.github.sophon.dreamcancel.domain.FEATURE_NAME
import io.github.sophon.dreamcancel.domain.FEATURE_URL
import io.github.sophon.wikiDreamCancel.BuildKonfig

object DreamCancelFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.KoFXV,
            Game.COTW,
        ),
        version = BuildKonfig.VERSION,
    )
}