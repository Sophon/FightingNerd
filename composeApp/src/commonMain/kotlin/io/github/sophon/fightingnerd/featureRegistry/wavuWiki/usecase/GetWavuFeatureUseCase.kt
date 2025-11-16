package io.github.sophon.fightingnerd.featureRegistry.wavuWiki.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.wikiwavu.WavuWikiClient

internal class GetWavuFeatureUseCase(
    private val wiki: WavuWikiClient,
) {
    fun invoke(): FeatureInfo = wiki.getFeatureInfo()
}