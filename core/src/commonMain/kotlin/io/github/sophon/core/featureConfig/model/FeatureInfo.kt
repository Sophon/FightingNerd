package io.github.sophon.core.featureConfig.model

data class FeatureInfo(
    val name: String,
    val url: String,
    val version: String,
    val iconUrl: String? = null,
    val feedbackDiscordChannelId: String? = null,
)
