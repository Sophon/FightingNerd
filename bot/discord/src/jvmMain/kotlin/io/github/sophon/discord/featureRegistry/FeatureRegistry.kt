package io.github.sophon.discord.featureRegistry

internal class FeatureRegistry(
    features: List<DiscordRegisteredFeature>,
) {
    private val featureMap = features.associateBy { it.featureInfo.name }

    fun getFeatures(names: List<String>): List<DiscordRegisteredFeature> {
        return names.mapNotNull { getFeature(it) }
    }

    private fun getFeature(name: String): DiscordRegisteredFeature? {
        return featureMap[name]
    }
}