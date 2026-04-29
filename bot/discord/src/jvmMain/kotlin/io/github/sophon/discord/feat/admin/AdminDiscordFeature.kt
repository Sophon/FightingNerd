package io.github.sophon.discord.feat.admin

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.onError
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.toFormattedString
import io.github.sophon.discord.feat.admin.usecase.BanUseCase
import io.github.sophon.discord.feat.admin.usecase.CreateRedirectButtonsUseCase
import io.github.sophon.discord.feat.admin.usecase.ProcessFeedbackUseCase
import io.github.sophon.discord.feat.admin.usecase.ReplyToFeedbackUseCase
import io.github.sophon.discord.feat.admin.usecase.StartAdminToolsUseCase
import io.github.sophon.discord.feat.admin.usecase.UnbanUseCase
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.domain.AdminFeatureInfo
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source
import io.github.sophon.domain.model.Ban
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class AdminDiscordFeature(
    adminFeatureInfo: AdminFeatureInfo,
    private val adminConfig: Config.AdminConfig,
    private val startAdminToolsUseCase: StartAdminToolsUseCase,
    private val processFeedbackUseCase: ProcessFeedbackUseCase,
    private val replyToFeedbackUseCase: ReplyToFeedbackUseCase,
    private val createRedirectButtonsUseCase: CreateRedirectButtonsUseCase,
    private val banUseCase: BanUseCase,
    private val unbanUseCase: UnbanUseCase,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
): DiscordRegisteredFeature, KoinComponent {
    private val featureList: List<FeatureInfo> by lazy {
        getKoin().get<List<DiscordRegisteredFeature>>().map { it.featureInfo }
    }

    override val featureInfo: FeatureInfo = adminFeatureInfo.featureInfo
    override val defaultCommand = null
    override val otherCommands = listOf(
        Command.Feedback,
    ) + adminCommands

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
        origin: Source,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.Feedback -> feedback(origin, message = query)
            Command.Reply -> reply(origin, query)
            Command.Ban -> ban(origin, query = query)
            Command.Unban -> unban(origin, query = query)
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun syncBans(): EmptyResult<BotError> {
        return startAdminToolsUseCase.invoke(adminConfig)
    }

    private suspend fun feedback(
        origin: Source,
        message: String,
    ): Result<BotOutput, BotError> {
        return processFeedbackUseCase.invoke(origin, message)
            .map { adminResult ->
                BotOutput(
                    feedback = BotOutput.Feedback(
                        embedBuilder = createFeedbackEmbed(adminResult, featureInfo),
                        origin = origin,
                        feedbackChannelList = adminConfig.feedbackChannelIdList,
                    ),
                    buttons = createRedirectButtonsUseCase.invoke(featureList)
                )
            }
    }

    private fun reply(origin: Source, query: String): Result<BotOutput, BotError> {
        return replyToFeedbackUseCase.invoke(origin, query)
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

    private suspend fun unban(
        origin: Source,
        query: String
    ): Result<BotOutput, BotError> {
        return unbanUseCase.invoke(origin, query)
            .map { target ->
                BotOutput(
                    reply = BotOutput.Reply(
                        embedBuilder = createBanStatusEmbed(),
                        target = target,
                    )
                )
            }
    }

    private fun banList(source: Source): Result<BotOutput, BotError> {
        TODO()
    }

    private fun createReplyEmbed(adminResult: AdminResult): EmbedBuilder.() -> Unit = {
        adminResult.apply {
            title = "Feedback response"
            color = Color(TURQUOISE)

            mandatoryField(
                name = "",
                value = message,
                inline = false,
            )

            featureFooter(featureInfo)
        }
    }

    private fun createBanStatusEmbed(ban: Ban? = null): EmbedBuilder.() -> Unit = {
        if (ban == null) {
            title = "Free like a bird! 🕊🕊🕊"
            mandatoryField(
                name = "Ban status",
                value = "UNBANNED",
                inline = false,
            )
            featureFooter(featureInfo)
        } else {
            title = "Chat shit, get banged! 🔥🔥🔥"
            mandatoryField(
                name = "Ban status",
                value = "BANNED 🔨: ${ban.bannedAt.toFormattedString()} → ${ban.expiresAt.toFormattedString()}",
                inline = false,
            )
            featureFooter(featureInfo)
        }
    }


    private companion object {
        const val TAG = "AdminDiscordFeature"
        const val TURQUOISE = 0x0000CED1
    }
}