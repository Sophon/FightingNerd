package io.github.sophon.discord.featureRegistry.wikiSuperCombo.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.wikiSuperCombo.SuperComboWikiClient

internal class GetSuperComboFeatureInfoUseCase(
    private val wiki: SuperComboWikiClient,
) {
    fun invoke(): FeatureInfo = wiki.getFeatureInfo()
}