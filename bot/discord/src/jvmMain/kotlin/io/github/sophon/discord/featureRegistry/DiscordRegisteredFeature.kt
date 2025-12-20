package io.github.sophon.discord.featureRegistry

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.feature.Game
import io.github.sophon.discord.BotError

internal interface DiscordRegisteredFeature {
    val featureInfo: FeatureInfo

    val defaultCommand: SupportedCommand?
    val otherCommands: List<SupportedCommand>

    fun registerGames(enabledGames: List<Game>) {
        //default: for when feature doesn't support games
    }

    suspend fun start()

    suspend fun execute(
        command: Command,
        query: String,
        authorId: String,
    ): Result<BotOutput, BotError>
}

data class SupportedCommand(
    val command: Command,
    val description: String,
    val arguments: List<Argument> = listOf(),
) {
    data class Argument(
        val name: String,
        val description: String,
        val isRequired: Boolean = true,
    )
}

data class BotOutput(
    val embedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val plainText: String? = null,
    val errorEmbedBuilder: (EmbedBuilder.() -> Unit)? = null,
    val images: Images? = null,
) {
    data class Images(
        val title: String,
        val titleUrl: String,
        val urls: List<String>,
    )
}