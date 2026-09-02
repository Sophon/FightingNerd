package io.github.sophon.discord.feat.bot

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.feat.bot.usecase.CreateJoinEmbedButtonUseCase
import io.github.sophon.discord.feat.core.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.config.FeatureRegistry
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.util.donationMessage
import io.github.sophon.integration.model.Source
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

internal class BotFeature(
    getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
    private val createJoinEmbedButtonUseCase: CreateJoinEmbedButtonUseCase,
    private val commandRegistry: CommandRegistry,
): DiscordRegisteredFeature, KoinComponent {
    private val featureRegistry: FeatureRegistry by inject()

    override val featureInfo: FeatureInfo = getBotFeatureInfoUseCase()
    override val defaultCommand = Command.Join
    override val otherCommands = listOf(
        Command.Tip,
        Command.Repo,
        Command.Invite,
        Command.Donate,
        Command.Help,
        Command.Commands,
        Command.Modules,
    )

    override suspend fun start() {
        Napier.d(tag = TAG) { "FightingNerd: ${featureInfo.version}" }
    }

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
        game: Game?,
    ): Result<BotOutput, BotError> {
        if (query.startsWith(URL_STEAM_LOBBY, ignoreCase = true)) {
            return createJoinEmbedButtonUseCase(origin, query)
        }

        return when (command) {
            Command.Tip,
            Command.Donate,
                -> {
                    Result.Success(BotOutput(primaryEmbedBuilder = tipEmbed(featureInfo)))
                }

            Command.Repo -> createRepoText()
            Command.Invite -> createInviteText()
            Command.Help -> createHelpEmbed()
            Command.Commands -> createCommandsEmbed()
            Command.Join -> createJoinEmbedButtonUseCase(origin, query)
            Command.Modules -> createModulesEmbed()

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }

    override suspend fun refreshData(): EmptyResult<BotError> {
        return Result.Success(Unit)
    }


    private fun createHelpEmbed(): Result<BotOutput, BotError> {
        val result = BotOutput(
            primaryEmbedBuilder = helpEmbed(commandRegistry, featureInfo),
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Commands",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Commands.name
                        ),
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            ),
        )

        return Result.Success(result)
    }

    private fun createModulesEmbed(): Result<BotOutput, BotError> {
        val featureList = featureRegistry.getRegisteredFeatures()
        val result = BotOutput(
            primaryEmbedBuilder = modulesEmbed(featureList, featureInfo),
        )

        return Result.Success(result)
    }

    private fun createCommandsEmbed(): Result<BotOutput, BotError> {
        val commandList = Command.entries.sortedBy { it.name }

        val result = BotOutput(
            primaryEmbedBuilder = commandsEmbed(commandList, commandRegistry, featureInfo),
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Examples",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Help.name
                        )
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(result)
    }

    private fun createRepoText(): Result<BotOutput, BotError> {
        return Result.Success(
            BotOutput(
                plainText = "Contribute to FightingNerd: $URL_REPO"
            )
        )
    }

    private fun createInviteText(): Result<BotOutput, BotError> {
        val text = "FightingNerd bot invite: $URL_INVITE\n" + donationMessage()
        return Result.Success(BotOutput(plainText = text))
    }


    private companion object Companion {
        const val TAG = "CoreDiscordFeature"
    }
}