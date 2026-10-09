package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.DiscordCommandInteraction
import io.github.sophon.discord.app.model.discord.Message
import io.github.sophon.discord.app.model.response.IgnoreResponse
import io.github.sophon.discord.app.service.ProcessUserInputService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ProcessUserInputServiceTest {
    //region message
    @Test
    fun `message from a bot is ignored`() = runTest {
        // given
        val expected = Result.Success(IgnoreResponse)
        val service = ProcessUserInputService(commandRouterService = RouterFixture().router)

        // when
        val result = service(message = message("<@$BOT_ID> char sol", isFromBot = true), botId = BOT_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `message without a leading tag is ignored`() = runTest {
        // given
        val expected = Result.Success(IgnoreResponse)
        val service = ProcessUserInputService(commandRouterService = RouterFixture().router)

        // when
        val result = service(message = message("did you see <@$BOT_ID> char sol"), botId = BOT_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `message tagging another bot is ignored`() = runTest {
        // given
        val expected = Result.Success(IgnoreResponse)
        val service = ProcessUserInputService(commandRouterService = RouterFixture().router)

        // when
        val result = service(message = message("<@159985870458322944> char sol"), botId = BOT_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `tag without a query is ignored`() = runTest {
        // given
        val expected = Result.Success(IgnoreResponse)
        val service = ProcessUserInputService(commandRouterService = RouterFixture().router)

        // when
        val result = service(message = message("<@$BOT_ID>   "), botId = BOT_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `ignored message reaches no service`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@$BOT_ID> char sol", isFromBot = true), botId = BOT_ID)

        // then
        assertThat(fixture.callList).isEmpty()
    }

    @Test
    fun `leading command word routes the rest as its query`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@$BOT_ID> char sol"), botId = BOT_ID)

        // then
        assertThat(fixture.callList).containsExactly("findCharacter(sol, true)")
    }

    @Test
    fun `nickname tag is a valid trigger`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@!$BOT_ID> char sol"), botId = BOT_ID)

        // then
        assertThat(fixture.callList).containsExactly("findCharacter(sol, true)")
    }

    @Test
    fun `command word alone has a blank query`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@$BOT_ID> alias"), botId = BOT_ID)

        // then
        assertThat(fixture.callList).containsExactly("findAliases()")
    }

    @Test
    fun `message without a command word is routed whole`() = runTest {
        // given
        val fixture = RouterFixture(moveService = FakeMoveService(frameDataResult = Result.Success(df1)))
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@$BOT_ID> jin df+1"), botId = BOT_ID)

        // then
        assertThat(fixture.callList).containsExactly("findFrameData(jin df+1)")
    }

    @Test
    fun `extra whitespace and command case are normalized`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@$BOT_ID>   CHAR \n  sol  "), botId = BOT_ID)

        // then
        assertThat(fixture.callList).containsExactly("findCharacter(sol, true)")
    }

    @Test
    fun `message author, channel and server are the source`() = runTest {
        // given
        val expected = UserRequest.Source(
            username = USERNAME,
            id = USER_ID,
            channelId = CHANNEL_ID,
            serverName = SERVER_NAME,
        )
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(message = message("<@$BOT_ID> feedback Heat Smash is off by one"), botId = BOT_ID)

        // then
        assertThat(fixture.callList).containsExactly("forwardFeedback(Heat Smash is off by one, $expected)")
    }
    //endregion

    //region slash command
    @Test
    fun `character argument goes first and autocomplete values stay encoded`() = runTest {
        // given
        val fixture = RouterFixture(moveService = FakeMoveService(frameDataResult = Result.Success(df1)))
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(interaction(command = "fd", argumentMap = mapOf("move" to "df+1", "character" to "jin::Tekken_8")))

        // then
        assertThat(fixture.callList).containsExactly("findFrameData(jin::Tekken_8 df+1)")
    }

    @Test
    fun `blank arguments are left out`() = runTest {
        // given
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(interaction(command = "startup", argumentMap = mapOf("character" to "jin", "min" to " ", "max" to "12")))

        // then
        assertThat(fixture.callList).containsExactly("findMovesInRange(jin 12, Startup)")
    }

    @Test
    fun `interaction user and channel are the source, missing server is blank`() = runTest {
        // given
        val expected = UserRequest.Source(username = USERNAME, id = USER_ID, channelId = CHANNEL_ID, serverName = "")
        val fixture = RouterFixture()
        val service = ProcessUserInputService(commandRouterService = fixture.router)

        // when
        service(interaction(command = "feedback", argumentMap = mapOf("feedback" to "Heat Smash is off by one")))

        // then
        assertThat(fixture.callList).containsExactly("forwardFeedback(Heat Smash is off by one, $expected)")
    }
    //endregion
}

private fun message(content: String, isFromBot: Boolean = false): Message {
    val message = Message(
        serverName = SERVER_NAME,
        channelId = CHANNEL_ID,
        author = Message.Author(id = USER_ID, username = USERNAME),
        isFromBot = isFromBot,
        content = content,
    )
    return message
}

private fun interaction(command: String, argumentMap: Map<String, String>): DiscordCommandInteraction {
    val interaction = DiscordCommandInteraction(
        username = USERNAME,
        userId = USER_ID,
        channelId = CHANNEL_ID,
        command = command,
        argumentMap = argumentMap,
    )
    return interaction
}


private const val BOT_ID = "1438716136790429776"
private const val USER_ID = "333333333333333333"
private const val USERNAME = "user"
private const val CHANNEL_ID = "555555555555555555"
private const val SERVER_NAME = "Tekken Zaibatsu"
private val df1 = moveResponse(input = "df+1", moveName = "Mid check")
