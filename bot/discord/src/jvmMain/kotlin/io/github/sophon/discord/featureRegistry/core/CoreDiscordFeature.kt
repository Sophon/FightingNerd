package io.github.sophon.discord.featureRegistry.core

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.URL_IMG_KOFI
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.util.mandatoryField

internal class CoreDiscordFeature(): DiscordRegisteredFeature {
    override val featureInfo = FeatureInfo(
        name = "Core Discord",
        url = "https://github.com/Sophon/Cornerman",
        iconUrl = null, //TODO: fill
    )
    override val defaultCommand = SupportedCommand(
        command = Command.TIP,
        description = "Dono arigato!",
        arguments = listOf(),
    )
    override val otherCommands = listOf<SupportedCommand>() //TODO: dono list

    override suspend fun start() {/* not needed */ }

    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return when (command) {
            Command.TIP -> Result.Success(createTipEmbed())
            else -> Result.Error(BotError.BOT_LOGIC_ERROR)
        }
    }


    private fun createTipEmbed(): EmbedBuilder.() -> Unit = {
        title = "Dono arigato!"
        url = URL_KOFI //TODO: load from config
        color = Color(PINK)

        mandatoryField(
            name = "💸💸💸",
            value = "I don't drink coffee but feel free to support the server costs!\n" +
                    URL_KOFI
        )

        footer {
            text = "Ko-Fi"
            icon = URL_IMG_KOFI
        }
    }

    private companion object {
        const val TAG = "CoreDiscordFeature"
        const val PINK = 0x00FF10F0
    }
}