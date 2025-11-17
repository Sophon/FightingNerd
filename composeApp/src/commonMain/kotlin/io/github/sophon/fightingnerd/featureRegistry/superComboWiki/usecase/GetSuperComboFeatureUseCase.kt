package io.github.sophon.fightingnerd.featureRegistry.superComboWiki.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient

internal class GetSuperComboFeatureUseCase(
    private val wiki: SuperComboWikiClient,
) {
    fun invoke(): FeatureInfo = wiki.getFeatureInfo()
}