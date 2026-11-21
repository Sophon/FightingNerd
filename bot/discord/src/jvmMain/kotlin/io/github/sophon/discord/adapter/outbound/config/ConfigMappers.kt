package io.github.sophon.discord.adapter.outbound.config

import io.github.sophon.core.architecture.DataError
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.discord.DiscordConfig

internal fun DiscordConfigDto.toDomain(): DiscordConfig {
    val discordConfig = DiscordConfig(
        featureList = featureList.map { it.toDomain() },
        adminConfig = adminConfig.toDomain(),
        statsConfig = statsConfig.toDomain(),
    )
    return discordConfig
}

internal fun FeatureDto.toDomain(): DiscordConfig.Feature {
    val feature = DiscordConfig.Feature(
        name = name,
        isEnabled = isEnabled,
        supportedGames = supportedGames,
    )
    return feature
}

internal fun AdminConfigDto.toDomain(): DiscordConfig.AdminConfig {
    val adminConfig = DiscordConfig.AdminConfig(
        administratorIdList = administratorIdList,
        feedbackChannelIdList = feedbackChannelIdList,
        adminServerId = adminServerId,
    )
    return adminConfig
}

internal fun StatsConfigDto.toDomain(): DiscordConfig.StatsConfig {
    val statsConfig = DiscordConfig.StatsConfig(
        isEnabled = isEnabled,
        statsChannelIdList = statsChannelIdList,
    )
    return statsConfig
}

internal fun DataError.Remote.toDomainError(): BotError {
    val botError = BotError.DownloadError(this.name)
    return botError
}
