package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.core.feature.FeatureInfo

internal class FeatureRegistry(
    private val featureListLoader: FeatureListLoader,
    private val fullFeatureList: List<ComposeRegisteredFeature>,
) {
    private val featureMap = mutableMapOf<String, ComposeRegisteredFeature>()

    suspend fun initialize() {
        val enabledFeatureNames = featureListLoader.loadFeatureList().featureList
            .filter { it.isEnabled }
            .map { it.name }
        val enabledFeatures = fullFeatureList.filter {
            it.featureInfo.name in enabledFeatureNames
        }

        featureMap.putAll(enabledFeatures.associateBy { it.featureInfo.name })
    }

    fun getFeatures(): List<ComposeRegisteredFeature> {
        return featureMap.values.toList()
    }

    fun getFeature(featureInfo: FeatureInfo): ComposeRegisteredFeature? {
        return featureMap[featureInfo.name]
    }
}