package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.core.feature.FeatureInfo

internal class FeatureRegistry(
    private val featureListLoader: FeatureListLoader,
    private val fullFeatureList: List<ComposeRegisteredFeature>,
) {
    private val featureMap = mutableMapOf<String, ComposeRegisteredFeature>()

    suspend fun initialize() {
        val config = featureListLoader.loadFeatureList()

        val enabledFeatureConfigs = config.featureList
            .filter { it.isEnabled }

        fullFeatureList.forEach { feature ->
            val featureConfig = enabledFeatureConfigs
                .find { it.name == feature.featureInfo.name }

            if (featureConfig != null) {
                feature.registerGames(featureConfig.supportedGames)
                featureMap[feature.featureInfo.name] = feature
            }
        }
    }

    fun getFeatures(): List<ComposeRegisteredFeature> = featureMap.values.toList()

    fun getFeature(featureInfo: FeatureInfo): ComposeRegisteredFeature? {
        return featureMap[featureInfo.name]
    }
}