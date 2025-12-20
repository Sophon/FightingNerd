package io.github.sophon.discord.featureRegistry.admin

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.Scheduler
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.featureRegistry.admin.usecase.ProcessFeedbackUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.StartAdminToolsUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.AdminResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class AdminDiscordFeature(
    adminFeatureInfo: AdminFeatureInfo,
    private val adminConfig: Config.AdminConfig,
    private val startAdminToolsUseCase: StartAdminToolsUseCase,
    private val processFeedbackUseCase: ProcessFeedbackUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature {
    override val featureInfo: FeatureInfo = adminFeatureInfo.featureInfo
    override val defaultCommand = null
    override val otherCommands = listOf(
        SupportedCommand(
            command = Command.FEEDBACK,
            description = "Provide feedback",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_FEEDBACK,
                    description = "Feedback"
                )
            )
        ),
        //TODO: ban, unban etc
    )

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        scheduler.start(
            task = ::syncBans,
        ).onEach { result ->
            result.onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    override suspend fun execute(
        command: Command,
        query: String,
        authorId: String,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FEEDBACK -> {
                feedback(authorId, query)
            }
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncBans(): EmptyResult<BotError> {
        return startAdminToolsUseCase.invoke(adminConfig)
    }

    private suspend fun feedback(
        authorId: String,
        message: String,
    ): Result<BotOutput, BotError> {
        return processFeedbackUseCase.invoke(authorId, message)
            .map { adminResult ->
                BotOutput(
                    feedback = BotOutput.Feedback(
                        embedBuilder = createFeedbackEmbed(adminResult),
                        authorId = authorId,
                        feedbackChannelList = adminConfig.feedbackChannelIdList
                    )
                )
            }
    }

    private fun createFeedbackEmbed(adminResult: AdminResult): EmbedBuilder.() -> Unit = {
        title = "from: ${adminResult.userId}"
        mandatoryField(
            name = "",
            value = adminResult.message,
            inline = false,
        )
    }


    private companion object {
        const val TAG = "AdminDiscordFeature"
        const val KEY_FEEDBACK = "feedback"
        const val PINK = 0x00FF10F0
    }
}