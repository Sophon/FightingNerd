package io.github.sophon.discord.feat.infilGlossary.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.glossaryinfil.integration.InfilGlossaryClient

@ExcludeFromCoverage("plain client call")
internal class GetInfilFeatureInfoUseCase(
    private val glossary: InfilGlossaryClient,
) {
    operator fun invoke(): FeatureInfo = glossary.getFeatureInfo()
}