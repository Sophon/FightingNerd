package io.github.sophon.discord.domain

internal class FeatureRegistry(
    features: List<DiscordRegisteredFeature>,
    private val coreFeature: DiscordRegisteredFeature,
) {
    private val featureMap = features.associateBy { it.featureInfo.name }

    fun getFeatures(names: List<String>): List<DiscordRegisteredFeature> {
        val configuredFeatures = names.mapNotNull { getFeature(it) }
        return configuredFeatures + coreFeature
    }

    fun getRegisteredFeatures(): List<DiscordRegisteredFeature> {
        return featureMap.values.toList()
    }

    private fun getFeature(name: String): DiscordRegisteredFeature? {
        return featureMap[name]
    }
}