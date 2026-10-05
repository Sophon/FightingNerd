package io.github.sophon.discord.app.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DiscordConfig(
    val featureList: List<Feature>,
    val adminConfig: AdminConfig,
    val statsConfig: StatsConfig,
) {
    @Serializable
    data class Feature(
        val name: String,
        val isEnabled: Boolean,
        val supportedGames: List<String>,
    )

    @Serializable
    data class AdminConfig(
        val administratorIdList: List<String>,
        val feedbackChannelIdList: List<String>,
        val adminServerId: String,
    )

    @Serializable
    data class StatsConfig(
        val isEnabled: Boolean,
        val statsChannelIdList: List<String>,
    )
}
