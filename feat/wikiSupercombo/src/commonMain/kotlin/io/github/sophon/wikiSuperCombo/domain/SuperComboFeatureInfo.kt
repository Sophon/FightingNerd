package io.github.sophon.wikiSuperCombo.domain

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.wikiSuperCombo.BuildKonfig
import io.github.sophon.wikiSuperCombo.FEATURE_IMG_URL
import io.github.sophon.wikiSuperCombo.FEATURE_NAME
import io.github.sophon.wikiSuperCombo.FEATURE_URL
import io.github.sophon.wikiSuperCombo.data.SuperComboTables

object SuperComboFeatureInfo {
    val featureInfo = FeatureInfo(
        name = FEATURE_NAME,
        url = FEATURE_URL,
        iconUrl = FEATURE_IMG_URL,
        supportedGames = SuperComboTables.supportedGames(),
        version = BuildKonfig.VERSION,
    )
}