package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.integration.model.Source

internal interface DiscordRegisteredFeature {
    val featureInfo: FeatureInfo

    val defaultCommand: Command?
    val otherCommands: List<Command>

    suspend fun start()

    suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError>

    suspend fun refreshData(): EmptyResult<BotError>
}
