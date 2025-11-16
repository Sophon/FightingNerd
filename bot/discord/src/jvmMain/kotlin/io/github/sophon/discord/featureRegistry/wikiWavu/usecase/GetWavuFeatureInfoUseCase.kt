package io.github.sophon.discord.featureRegistry.wikiWavu.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.wikiwavu.WavuWikiClient

internal class GetWavuFeatureInfoUseCase(
    private val wiki: WavuWikiClient,
) {
    fun invoke(): FeatureInfo = wiki.getFeatureInfo()
}