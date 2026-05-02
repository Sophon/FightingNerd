package io.github.sophon.fightingnerd.featureRegistry

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.fightingnerd.feat.moduleList.model.WikiModule

internal class FeatureRegistry(
    private val featureListLoader: FeatureListLoader,
    private val fullFeatureList: List<WikiModule>,
) {
    private val featureMap = mutableMapOf<String, WikiModule>()

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

    fun getFeatures(): List<WikiModule> = featureMap.values.toList()

    fun getFeature(featureInfo: FeatureInfo): WikiModule? {
        return featureMap[featureInfo.name]
    }
}