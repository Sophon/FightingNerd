package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.ModerationRequest
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.DiscordConfig
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.FeedbackResponse
import io.github.sophon.discord.app.model.response.PlainTextResponse
import io.github.sophon.discord.app.model.response.ReplyResponse
import io.github.sophon.discord.app.outPort.AdminPort
import io.github.sophon.discord.app.outPort.BanPort
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.app.service.AdminServiceImpl
import io.github.sophon.discord.inPort.RefreshWikiUseCase
import io.github.sophon.wiki.model.wiki.Wiki
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class AdminServiceTest {
    //region isAdmin
    @Test
    fun `listed user is admin`() = runTest {
        // given
        val expected = Result.Success(true)
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.isAdmin(ADMIN_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unlisted user is not admin`() = runTest {
        // given
        val expected = Result.Success(false)
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.isAdmin(USER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region forwardFeedback
    @Test
    fun `feedback without a source is a logic error`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.forwardFeedback(query = FEEDBACK_MESSAGE, source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `blank feedback is an invalid query`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.forwardFeedback(query = " ", source = user)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `banned user can't send feedback`() = runTest {
        // given
        val service = adminService(
            coroutineScope = backgroundScope,
            banPort = FakeBanPort(bannedIdList = listOf(USER_ID)),
        )

        // when
        val result = service.forwardFeedback(query = FEEDBACK_MESSAGE, source = user)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.UserBanned::class)
    }

    @Test
    fun `feedback goes to the configured feedback channels`() = runTest {
        // given
        val expected = Result.Success(
            FeedbackResponse(
                author = user,
                message = FEEDBACK_MESSAGE,
                feedbackChannelIdList = listOf(FEEDBACK_CHANNEL_ID),
                dataSource = BOT_DATA_SOURCE,
                buttonSet = BotResponse.ButtonSet(
                    buttonList = emptyList(),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                ),
            ),
        )
        val service = adminService(
            coroutineScope = backgroundScope,
            loadConfigPort = FakeLoadConfigPort(Result.Success(discordConfig(featureList = emptyList()))),
        )

        // when
        val result = service.forwardFeedback(query = FEEDBACK_MESSAGE, source = user)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `feedback buttons redirect once to each wiki with a feedback channel`() = runTest {
        // given
        val expected = listOf(redirectButton(Wiki.Wavu), redirectButton(Wiki.DustLoop))
        val featureList = listOf(
            DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
            DiscordConfig.Feature(name = "SuperCombo Wiki", isEnabled = true, supportedGames = listOf("Street_Fighter_6")),
            DiscordConfig.Feature(name = "DustLoop Wiki", isEnabled = true, supportedGames = listOf("GGST", "GBVSR")),
        )
        val service = adminService(
            coroutineScope = backgroundScope,
            loadConfigPort = FakeLoadConfigPort(Result.Success(discordConfig(featureList = featureList))),
        )

        // when
        val result = service.forwardFeedback(query = FEEDBACK_MESSAGE, source = user)

        // then
        val buttonList = (result as Result.Success).data.buttonSet.buttonList
        assertThat(buttonList).isEqualTo(expected)
    }

    @Test
    fun `disabled wiki gets no feedback button`() = runTest {
        // given
        val expected = listOf(redirectButton(Wiki.Wavu))
        val featureList = listOf(
            DiscordConfig.Feature(name = "Wavu Wiki", isEnabled = true, supportedGames = listOf("Tekken_8")),
            DiscordConfig.Feature(name = "DustLoop Wiki", isEnabled = false, supportedGames = listOf("GGST")),
        )
        val service = adminService(
            coroutineScope = backgroundScope,
            loadConfigPort = FakeLoadConfigPort(Result.Success(discordConfig(featureList = featureList))),
        )

        // when
        val result = service.forwardFeedback(query = FEEDBACK_MESSAGE, source = user)

        // then
        val buttonList = (result as Result.Success).data.buttonSet.buttonList
        assertThat(buttonList).isEqualTo(expected)
    }

    @Test
    fun `failed config load returns the load error`() = runTest {
        // given
        val expected = Result.Error(BotError.FileError("discordConfig.json"))
        val service = adminService(
            coroutineScope = backgroundScope,
            loadConfigPort = FakeLoadConfigPort(expected),
        )

        // when
        val result = service.forwardFeedback(query = FEEDBACK_MESSAGE, source = user)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region replyToFeedback
    @Test
    fun `reply without a source is a logic error`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.replyToFeedback(query = "$USER_HANDLE $REPLY_MESSAGE", source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `reply to a malformed recipient is an invalid query`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.replyToFeedback(query = "user $REPLY_MESSAGE", source = admin)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `reply without a message is an invalid query`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.replyToFeedback(query = USER_HANDLE, source = admin)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `non-admin can't reply`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.replyToFeedback(query = "$USER_HANDLE $REPLY_MESSAGE", source = user)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.PermissionDenied::class)
    }

    @Test
    fun `admin reply goes to the recipient`() = runTest {
        // given
        val expected = Result.Success(
            ReplyResponse(
                recipient = user,
                message = REPLY_MESSAGE,
                dataSource = BOT_DATA_SOURCE,
            ),
        )
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.replyToFeedback(query = "$USER_HANDLE $REPLY_MESSAGE", source = admin)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region refreshWiki
    @Test
    fun `refresh without a source is a logic error`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.refreshWiki(source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `non-admin can't refresh`() = runTest {
        // given
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.refreshWiki(source = user)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.PermissionDenied::class)
    }

    @Test
    fun `non-admin refresh doesn't start`() = runTest {
        // given
        val refreshWikiUseCase = FakeRefreshWikiUseCase()
        val service = adminService(coroutineScope = backgroundScope, refreshWikiUseCase = refreshWikiUseCase)

        // when
        service.refreshWiki(source = user)
        runCurrent()

        // then
        assertThat(refreshWikiUseCase.callCount).isEqualTo(0)
    }

    @Test
    fun `admin refresh is confirmed`() = runTest {
        // given
        val expected = Result.Success(PlainTextResponse(text = "Wiki refresh started"))
        val service = adminService(coroutineScope = backgroundScope)

        // when
        val result = service.refreshWiki(source = admin)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `admin refresh runs once`() = runTest {
        // given
        val refreshWikiUseCase = FakeRefreshWikiUseCase()
        val service = adminService(coroutineScope = backgroundScope, refreshWikiUseCase = refreshWikiUseCase)

        // when
        service.refreshWiki(source = admin)
        runCurrent()

        // then
        assertThat(refreshWikiUseCase.callCount).isEqualTo(1)
    }
    //endregion


    private class FakeAdminPort(
        private val adminIdList: List<String>,
    ): AdminPort {
        override fun isUserAdmin(userId: String): Result<Boolean, BotError> = Result.Success(userId in adminIdList)
    }

    private class FakeBanPort(
        private val bannedIdList: List<String> = emptyList(),
    ): BanPort {
        override suspend fun ban(moderationRequest: ModerationRequest): Result<BanResponse, BotError> =
            Result.Error(BotError.NotImplemented("ban"))

        override suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<BotError> =
            Result.Error(BotError.NotImplemented("unban"))

        override suspend fun isBanned(userId: String): Result<Boolean, BotError> = Result.Success(userId in bannedIdList)
    }

    private class FakeLoadConfigPort(
        private val result: Result<DiscordConfig, BotError>,
    ): LoadConfigPort {
        override fun load(): Result<DiscordConfig, BotError> = result
    }

    private class FakeRefreshWikiUseCase: RefreshWikiUseCase {
        var callCount = 0

        override suspend fun invoke() {
            callCount++
        }
    }

    private fun adminService(
        coroutineScope: CoroutineScope,
        banPort: FakeBanPort = FakeBanPort(),
        loadConfigPort: FakeLoadConfigPort = FakeLoadConfigPort(Result.Success(discordConfig(featureList = emptyList()))),
        refreshWikiUseCase: FakeRefreshWikiUseCase = FakeRefreshWikiUseCase(),
    ): AdminServiceImpl {
        val service = AdminServiceImpl(
            adminPort = FakeAdminPort(adminIdList = listOf(ADMIN_ID)),
            banPort = banPort,
            loadConfigPort = loadConfigPort,
            refreshWikiUseCase = refreshWikiUseCase,
            coroutineScope = coroutineScope,
        )
        return service
    }
}

private fun discordConfig(featureList: List<DiscordConfig.Feature>): DiscordConfig {
    val discordConfig = DiscordConfig(
        featureList = featureList,
        adminConfig = DiscordConfig.AdminConfig(
            administratorIdList = listOf(ADMIN_ID),
            feedbackChannelIdList = listOf(FEEDBACK_CHANNEL_ID),
            adminServerId = "777777777777777777",
        ),
        statsConfig = DiscordConfig.StatsConfig(isEnabled = false, statsChannelIdList = emptyList()),
    )
    return discordConfig
}

private fun redirectButton(wiki: Wiki): BotResponse.EmbedButton {
    val button = BotResponse.EmbedButton(
        label = wiki.displayName,
        action = BotResponse.EmbedButton.Action.Redirect(wiki.feedbackDiscordChannelId.orEmpty()),
    )
    return button
}


private const val ADMIN_ID = "111111111111111111"
private const val USER_ID = "333333333333333333"
private const val CHANNEL_ID = "555555555555555555"
private const val FEEDBACK_CHANNEL_ID = "666666666666666666"
private const val USER_HANDLE = "user-$USER_ID-$CHANNEL_ID"
private const val FEEDBACK_MESSAGE = "Heat Smash frame data is off by one"
private const val REPLY_MESSAGE = "Thanks, fixed in the next release"
private val admin = UserRequest.Source(username = "admin", id = ADMIN_ID, channelId = CHANNEL_ID)
private val user = UserRequest.Source(username = "user", id = USER_ID, channelId = CHANNEL_ID)
