package io.github.sophon.discord.adapter.outbound.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DiscordConfigDto(
    @SerialName("featureList") val featureList: List<FeatureDto>,
    @SerialName("adminConfig") val adminConfig: AdminConfigDto,
    @SerialName("statsConfig") val statsConfig: StatsConfigDto,
)

@Serializable
internal data class FeatureDto(
    @SerialName("name") val name: String,
    @SerialName("isEnabled") val isEnabled: Boolean,
    @SerialName("supportedGames") val supportedGames: List<String>,
)

@Serializable
internal data class AdminConfigDto(
    @SerialName("administratorIdList") val administratorIdList: List<String>,
    @SerialName("feedbackChannelIdList") val feedbackChannelIdList: List<String>,
    @SerialName("adminServerId") val adminServerId: String,
)

@Serializable
internal data class StatsConfigDto(
    @SerialName("isEnabled") val isEnabled: Boolean,
    @SerialName("statsChannelIdList") val statsChannelIdList: List<String>,
)
