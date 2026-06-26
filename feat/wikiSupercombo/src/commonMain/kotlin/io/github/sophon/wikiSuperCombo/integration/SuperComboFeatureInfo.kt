package io.github.sophon.wikiSuperCombo.integration

import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wikiSuperCombo.BuildKonfig
import io.github.sophon.wikiSuperCombo.domain.FEATURE_IMG_URL
import io.github.sophon.wikiSuperCombo.domain.FEATURE_NAME
import io.github.sophon.wikiSuperCombo.domain.FEATURE_URL

object SuperComboFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGameSet = setOf(
            Game.StreetFighter6,
            Game.MK1,
            Game.AVL,
        ),
        version = BuildKonfig.VERSION,
    )
}