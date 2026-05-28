package io.github.sophon.discord.feat.bot

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.feat.bot.usecase.CreateJoinEmbedButtonUseCase
import io.github.sophon.discord.feat.bot.usecase.GetBotFeatureInfoUseCase
import io.github.sophon.discord.feat.config.FeatureRegistry
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.integration.model.Source
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

internal class BotFeature(
    getBotFeatureInfoUseCase: GetBotFeatureInfoUseCase,
    private val createJoinEmbedButtonUseCase: CreateJoinEmbedButtonUseCase,
): DiscordRegisteredFeature, KoinComponent {
    private val featureRegistry: FeatureRegistry by inject()

    override val featureInfo: FeatureInfo = getBotFeatureInfoUseCase.invoke()
    override val defaultCommand = null
    override val otherCommands = listOf(
        Command.Tip,
        Command.Repo,
        Command.Invite,
        Command.Donate,
        Command.Help,
        Command.Commands,
        Command.Examples,
        Command.Join,
        Command.Modules,
        Command.Alias,
    )

    override suspend fun start() {
        Napier.d(tag = TAG) { "FightingNerd: ${featureInfo.version}" }
    }

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
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
            Command.Examples -> createExamples()
            Command.Join -> createJoinEmbedButtonUseCase.invoke(origin, query)
            Command.Modules -> createModulesEmbed()
            Command.Alias -> createAliasEmbed()

            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }

    override suspend fun refreshData(): EmptyResult<BotError> {
        return Result.Success(Unit)
    }


    private fun createHelpEmbed(): Result<BotOutput, BotError> {
        val result = BotOutput(
            primaryEmbedBuilder = helpEmbed(featureInfo),
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Commands",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Commands.name
                        ),
                    ),
                    BotOutput.EmbedButton(
                        label = "Examples",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Examples.name
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
        val commands = Command.entries.sortedBy { it.name }

        val result = BotOutput(
            primaryEmbedBuilder = commandsEmbed(commands, featureInfo),
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Examples",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Examples.name
                        )
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(result)
    }

    private fun createAliasEmbed(): Result<BotOutput, BotError> {
        val commandList = Command.entries
            .sortedBy { it.name }
            .filterNot { it == Command.Alias }
            .filter { it.name.startsWith("Alias") }
        val buttonList = commandList.map { command ->
            BotOutput.EmbedButton(
                label = command.name,
                action = BotOutput.EmbedButton.Action.Query(command.name)
            )
        }

        val result = BotOutput(
            primaryEmbedBuilder = aliasEmbed(commandList, featureInfo),
            buttons = BotOutput.ButtonSet(
                buttonList = buttonList,
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
        return Result.Success(
            BotOutput(
                plainText = "FightingNerd bot invite: $URL_INVITE"
            )
        )
    }

    private fun createExamples(): Result<BotOutput, BotError> {
        val result = BotOutput(
            primaryEmbedBuilder = examplesEmbed(featureInfo),
            buttons = BotOutput.ButtonSet(
                buttonList = listOf(
                    BotOutput.EmbedButton(
                        label = "Commands",
                        action = BotOutput.EmbedButton.Action.Query(
                            Command.Commands.name
                        ),
                    )
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            )
        )

        return Result.Success(result)
    }

    private companion object Companion {
        const val TAG = "CoreDiscordFeature"
    }
}