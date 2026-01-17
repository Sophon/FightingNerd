package io.github.sophon.discord.domain

import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.discord.BotError
import io.github.sophon.domain.Source
import kotlinx.coroutines.flow.SharedFlow

internal interface DiscordRegisteredFeature {
    val featureInfo: FeatureInfo

    val defaultCommand: SupportedCommand?
    val otherCommands: List<SupportedCommand>

    val events: SharedFlow<Result<BotOutput, BotError>>

    fun registerGames(enabledGames: List<Game>) {
        //default: for when feature doesn't support games
    }

    suspend fun start()

    suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    )
}
