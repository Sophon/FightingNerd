package io.github.sophon.discord.featureRegistry.infilGlossary.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.glossaryinfil.InfilGlossaryClient

internal class GetInfilFeatureInfoUseCase(
    private val glossary: InfilGlossaryClient,
) {
    fun invoke(): FeatureInfo = glossary.getFeatureInfo()
}