package io.github.sophon.discord.featureRegistry.core

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.URL_IMG_KOFI
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.featureRegistry.BotOutput
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
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.TIP,
            Command.DONATE,
                -> createTipEmbed()

            Command.REPO -> createRepoEmbed()
            Command.INVITE -> createInviteEmbed()
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private fun createTipEmbed(): Result<BotOutput, BotError> {
        val embedBuilder: EmbedBuilder.() -> Unit = {
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

        return Result.Success(BotOutput(embedBuilder))
    }

    private fun createRepoEmbed(): Result<BotOutput, BotError> {
        return Result.Success(
            BotOutput(
                plainText = "Contribute to FightingNerd: $URL_REPO"
            )
        )
    }

    private fun createInviteEmbed(): Result<BotOutput, BotError> {
        return Result.Success(
            BotOutput(
                plainText = "FightingNerd bot invite: $URL_INVITE"
            )
        )
    }

    private companion object {
        const val TAG = "CoreDiscordFeature"
        const val PINK = 0x00FF10F0
    }
}