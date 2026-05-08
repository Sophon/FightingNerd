package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.core.model.Module

internal class FeatureRegistry(
    private val featureListLoader: FeatureListLoader,
    private val fullFeatureList: List<Module>,
) {
    private val featureMap = mutableMapOf<String, Module>()

    suspend fun initialize() {
        val config = featureListLoader.loadFeatureList()

        val enabledFeatureConfigs = config.featureList
            .filter { it.isEnabled }

        fullFeatureList.forEach { feature ->
            val featureConfig = enabledFeatureConfigs
                .find { it.name == feature.featureInfo.name }

            if (featureConfig != null) {
                feature.registerGames(featureConfig.supportedGameList)
                featureMap[feature.featureInfo.name] = feature
            }
        }
    }

    fun getFeatures(): List<Module> = featureMap.values.toList()

    fun getFeature(featureInfo: FeatureInfo): Module? {
        return featureMap[featureInfo.name]
    }
}