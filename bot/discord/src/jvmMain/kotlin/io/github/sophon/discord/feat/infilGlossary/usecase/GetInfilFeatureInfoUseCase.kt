package io.github.sophon.discord.feat.infilGlossary.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.glossaryinfil.integration.InfilGlossaryClient

internal class GetInfilFeatureInfoUseCase(
    private val glossary: InfilGlossaryClient,
) {
    fun invoke(): FeatureInfo = glossary.getFeatureInfo()
}