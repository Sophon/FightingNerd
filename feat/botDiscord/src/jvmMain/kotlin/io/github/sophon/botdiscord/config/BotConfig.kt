package io.github.sophon.botdiscord.config

import kotlinx.serialization.Serializable

@Serializable
data class BotConfig(
    val featureList: List<FeatureConfig>,
) {
    @Serializable
    data class FeatureConfig(
        val name: String,
        val isEnabled: Boolean,
    )
}
