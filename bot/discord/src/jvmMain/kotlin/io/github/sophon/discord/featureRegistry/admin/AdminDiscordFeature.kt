package io.github.sophon.discord.featureRegistry.admin

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.BotError
import io.github.sophon.discord.MAX_LENGTH_EMBED
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.Scheduler
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.featureRegistry.admin.usecase.BanUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.ProcessFeedbackUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.ReplyToFeedbackUseCase
import io.github.sophon.discord.featureRegistry.admin.usecase.StartAdminToolsUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source
import io.github.sophon.domain.model.Ban
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class AdminDiscordFeature(
    adminFeatureInfo: AdminFeatureInfo,
    private val adminConfig: Config.AdminConfig,
    private val startAdminToolsUseCase: StartAdminToolsUseCase,
    private val processFeedbackUseCase: ProcessFeedbackUseCase,
    private val replyToFeedbackUseCase: ReplyToFeedbackUseCase,
    private val banUseCase: BanUseCase,
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
        SupportedCommand(
            command = Command.REPLY,
            description = "Answer feedback",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_REPLY_RECIPIENT,
                    description = "username-id-serverId",
                ),
                SupportedCommand.Argument(
                    name = KEY_REPLY_MESSAGE,
                    description = "Reply",
                )
            )
        ),
        SupportedCommand(
            command = Command.BAN,
            description = "Ban user",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_REPLY_BAN,
                    description = "User ID",
                )
            ),
        ),
        SupportedCommand(
            command = Command.UNBAN,
            description = "Unban user",
            arguments = listOf(
                SupportedCommand.Argument(
                    name = KEY_REPLY_UNBAN,
                    description = "User ID",
                )
            ),
        ),
        SupportedCommand(
            command = Command.BANLIST,
            description = "List of banned users",
            arguments = listOf(),
        )
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
        source: Source,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.FEEDBACK -> feedback(origin = source, message = query)
            Command.REPLY -> reply(query)
            Command.BAN -> ban(origin = source, query = query)
            Command.UNBAN -> unban(source)
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncBans(): EmptyResult<BotError> {
        return startAdminToolsUseCase.invoke(adminConfig)
    }

    private fun feedback(
        origin: Source,
        message: String,
    ): Result<BotOutput, BotError> {
        return processFeedbackUseCase.invoke(origin, message)
            .map { adminResult ->
                BotOutput(
                    feedback = BotOutput.Feedback(
                        embedBuilder = createFeedbackEmbed(adminResult),
                        origin = origin,
                        feedbackChannelList = adminConfig.feedbackChannelIdList
                    )
                )
            }
    }

    private fun reply(query: String): Result<BotOutput, BotError> {
        return replyToFeedbackUseCase.invoke(query)
            .map { adminResult ->
                BotOutput(
                    reply = BotOutput.Reply(
                        embedBuilder = createReplyEmbed(adminResult),
                        target = adminResult.source,
                    )
                )
            }
    }

    private suspend fun ban(
        origin: Source,
        query: String
    ): Result<BotOutput, BotError> {
        return banUseCase.invoke(origin, query)
            .map { pair ->
                val (ban, target) = pair
                BotOutput(
                    reply = BotOutput.Reply(
                        embedBuilder = createBanStatusEmbed(ban),
                        target = target,
                    )
                )
            }
    }

    private fun unban(source: Source): Result<BotOutput, BotError> {
        TODO()
    }

    private fun banList(source: Source): Result<BotOutput, BotError> {
        TODO()
    }

    private fun createFeedbackEmbed(adminResult: AdminResult): EmbedBuilder.() -> Unit = {
        adminResult.apply {
            title = "${source.username}-${source.id}-${source.channelId}"
            color = Color(TURQUOISE)

            mandatoryField(
                name = "",
                value = message?.truncate(MAX_LENGTH_EMBED),
                inline = false,
            )
        }
    }

    private fun createReplyEmbed(adminResult: AdminResult): EmbedBuilder.() -> Unit = {
        adminResult.apply {
            title = "Feedback response"
            color = Color(TURQUOISE)

            mandatoryField(
                name = "",
                value = message?.truncate(MAX_LENGTH_EMBED),
                inline = false,
            )
        }
    }

    private fun createBanStatusEmbed(ban: Ban): EmbedBuilder.() -> Unit = {
        TODO()
    }


    private companion object {
        const val TAG = "AdminDiscordFeature"
        const val KEY_FEEDBACK = "feedback"
        const val KEY_REPLY_RECIPIENT = "recipient"
        const val KEY_REPLY_MESSAGE = "message"
        const val KEY_REPLY_BAN = "ban"
        const val KEY_REPLY_UNBAN = "unban"
        const val TURQUOISE = 0x0000CED1
    }
}