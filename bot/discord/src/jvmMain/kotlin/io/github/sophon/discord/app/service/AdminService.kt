package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.FeedbackResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.ReplyResponse
import io.github.sophon.discord.app.model.Command
import io.github.sophon.discord.app.model.DiscordConfig
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.outPort.AdminPort
import io.github.sophon.discord.app.outPort.BanPort
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.inPort.RefreshWikiUseCase
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal interface AdminService {
    fun isAdmin(userId: String): Result<Boolean, BotError>

    suspend fun forwardFeedback(
        query: String,
        source: UserRequest.Source?,
    ): Result<FeedbackResponse, BotError>

    fun replyToFeedback(
        query: String,
        source: UserRequest.Source?,
    ): Result<ReplyResponse, BotError>

    /**
     * Starts the refresh in the background and confirms right away - a refresh outlasts Discord's response window.
     */
    fun refreshWiki(source: UserRequest.Source?): Result<PlainTextResponse, BotError>
}

internal class AdminServiceImpl(
    private val adminPort: AdminPort,
    private val banPort: BanPort,
    private val loadConfigPort: LoadConfigPort,
    private val refreshWikiUseCase: RefreshWikiUseCase,
    private val coroutineScope: CoroutineScope,
): AdminService {
    override fun isAdmin(userId: String): Result<Boolean, BotError> {
        return adminPort.isUserAdmin(userId)
    }

    override suspend fun forwardFeedback(
        query: String,
        source: UserRequest.Source?,
    ): Result<FeedbackResponse, BotError> {
        val result = when {
            (source == null) -> Result.Error(BotError.BotLogicError(Command.Feedback.name, query))
            query.isBlank() -> Result.Error(BotError.InvalidQuery(query))
            else -> createFeedback(author = source, message = query)
        }
        return result
    }

    override fun replyToFeedback(
        query: String,
        source: UserRequest.Source?,
    ): Result<ReplyResponse, BotError> {
        val recipient = UserRequest.Source.parse(query.substringBefore(' '))
        val message = query.substringAfter(delimiter = " ", missingDelimiterValue = "").trim()

        val result = when {
            (source == null) -> Result.Error(BotError.BotLogicError(Command.Reply.name, query))
            (recipient == null) || (message.isBlank()) -> Result.Error(BotError.InvalidQuery(query))
            else -> createReply(issuerId = source.id, recipient = recipient, message = message)
        }
        return result
    }

    override fun refreshWiki(source: UserRequest.Source?): Result<PlainTextResponse, BotError> {
        val result = if (source == null) {
            Result.Error(BotError.BotLogicError(Command.Refresh.name))
        } else {
            isAdmin(source.id)
                .flatMap { isAdmin ->
                    val refreshResult = if (isAdmin) {
                        coroutineScope.launch { refreshWikiUseCase() }
                        Result.Success(PlainTextResponse(text = "Wiki refresh started"))
                    } else {
                        Result.Error(BotError.PermissionDenied())
                    }
                    refreshResult
                }
        }
        return result
    }


    private suspend fun createFeedback(
        author: UserRequest.Source,
        message: String,
    ): Result<FeedbackResponse, BotError> {
        val result = banPort.isBanned(author.id)
            .flatMap { isBanned ->
                val feedbackResult = if (isBanned) {
                    Result.Error(BotError.UserBanned(author.id))
                } else {
                    loadConfigPort.load()
                        .map { discordConfig ->
                            FeedbackResponse(
                                author = author,
                                message = message,
                                feedbackChannelIdList = discordConfig.adminConfig.feedbackChannelIdList,
                                buttonSet = createForwardButtonSet(discordConfig.featureList),
                            )
                        }
                }
                feedbackResult
            }
        return result
    }

    private fun createForwardButtonSet(featureList: List<DiscordConfig.Feature>): BotResponse.ButtonSet {
        val buttonList = featureList
            .asSequence()
            .filter { it.isEnabled }
            .flatMap { it.supportedGames }
            .mapNotNull { gameId -> Game.fromId(gameId) }
            .map { it.wiki }
            .distinct()
            .mapNotNull { wiki ->
                wiki.feedbackDiscordChannelId?.let { channelId ->
                    BotResponse.EmbedButton(
                        label = wiki.displayName,
                        action = BotResponse.EmbedButton.Action.Redirect(channelId),
                    )
                }
            }
            .toList()

        val buttonSet = BotResponse.ButtonSet(
            buttonList = buttonList,
            duration = EMBED_BUTTON_DURATION_INF.seconds,
        )
        return buttonSet
    }

    private fun createReply(
        issuerId: String,
        recipient: UserRequest.Source,
        message: String,
    ): Result<ReplyResponse, BotError> {
        val result = isAdmin(issuerId)
            .flatMap { isAdmin ->
                val replyResult = if (isAdmin) {
                    Result.Success(ReplyResponse(recipient = recipient, message = message))
                } else {
                    Result.Error(BotError.PermissionDenied())
                }
                replyResult
            }
        return result
    }
}
