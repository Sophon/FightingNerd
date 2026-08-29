package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.integration.model.Source

internal interface DiscordRegisteredFeature {
    val featureInfo: FeatureInfo

    val defaultCommand: Command?
    val otherCommands: List<Command>

    suspend fun start()

    /**
     * @param game supplied when the caller already knows which game the query targets
     * (e.g. autocomplete provided an encoded character value). Wiki features can then
     * route straight to their [Game]-scoped wiki; non-game features ignore it.
     */
    suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
        game: Game? = null,
    ): Result<BotOutput, BotError>

    suspend fun refreshData(): EmptyResult<BotError>
}
