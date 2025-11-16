package io.github.sophon.discord.featureRegistry

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError

interface DiscordRegisteredFeature {
    val featureInfo: FeatureInfo

    val defaultCommand: SupportedCommand?
    val otherCommands: List<SupportedCommand>

    suspend fun start()

    suspend fun execute(
        command: Command,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError>
}

data class SupportedCommand(
    val command: Command,
    val description: String,
    val arguments: List<Argument>
) {
    data class Argument(
        val name: String,
        val description: String,
        val isRequired: Boolean = true,
    )
}
