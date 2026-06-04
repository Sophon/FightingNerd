package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
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
