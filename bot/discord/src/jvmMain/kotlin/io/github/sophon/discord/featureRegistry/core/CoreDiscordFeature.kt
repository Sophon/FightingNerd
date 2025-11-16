package io.github.sophon.discord.featureRegistry.core

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.URL_IMG_DISCORD
import io.github.sophon.discord.URL_IMG_GITHUB
import io.github.sophon.discord.URL_IMG_KOFI
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.util.mandatoryField

internal class CoreDiscordFeature(
    getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
): DiscordRegisteredFeature {
    override val featureInfo: FeatureInfo = getBotFeatureInfoUseCase.invoke()
    override val defaultCommand = null
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.TIP,
            description = "Dono arigato!",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.REPO,
            description = "Project repository",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.INVITE,
            description = "Bot invite link",
            arguments = listOf(),
        ),
        SupportedCommand(
            command = Command.DONATE,
            description = "Dono arigato!",
            arguments = listOf(),
        )
    )

    override suspend fun start() {
        Napier.d(tag = TAG) { "FightingNerd: ${featureInfo.version}" }
    }

    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return when (command) {
            Command.TIP,
            Command.DONATE,
                -> Result.Success(createTipEmbed())

            Command.REPO -> Result.Success(createRepoEmbed())
            Command.INVITE -> Result.Success(createInviteEmbed())
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

    private fun createRepoEmbed(): EmbedBuilder.() -> Unit = {
        title = "GitHub repo"
        url = URL_REPO
        color = Color(BLACK)

        mandatoryField(
            name = "Project repository",
            value = URL_REPO
        )

        footer {
            text = "GitHub"
            icon = URL_IMG_GITHUB
        }
    }

    private fun createInviteEmbed(): EmbedBuilder.() -> Unit = {
        title = "Fighting Nerd bot"
        url = URL_REPO
        color = Color(BLURPLE)

        mandatoryField(
            name = "Bot invite",
            value = URL_INVITE,
        )

        footer {
            text = "Discord"
            icon = URL_IMG_DISCORD
        }
    }

    private companion object {
        const val TAG = "CoreDiscordFeature"
        const val BLACK = 0x000D1117
        const val BLURPLE = 0x005865F2
        const val PINK = 0x00FF10F0
    }
}