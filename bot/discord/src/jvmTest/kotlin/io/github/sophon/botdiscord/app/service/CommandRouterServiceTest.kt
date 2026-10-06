package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.frameData.MoveType
import io.github.sophon.discord.app.model.response.ListResponse
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CommandRouterServiceTest {
    //region routing
    @Test
    fun `fd finds frame data`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        fixture.router(command = Command.Fd, query = "jin df+1", source = user)

        // then
        assertThat(fixture.callList).containsExactly("findFrameData(jin df+1)")
    }

    @Test
    fun `move type commands find moves of their type`() = runTest {
        // given
        val expected = listOf(
            "findMovesOfType(jin, ${MoveType.PC})",
            "findMovesOfType(jin, ${MoveType.HEAT})",
            "findMovesOfType(jin, ${MoveType.HOMING})",
        )
        val fixture = RouterFixture()

        // when
        listOf(Command.Pc, Command.Heat, Command.Homing).forEach { command ->
            fixture.router(command = command, query = "jin", source = user)
        }

        // then
        assertThat(fixture.callList).isEqualTo(expected)
    }

    @Test
    fun `range commands find moves in range`() = runTest {
        // given
        val expected = listOf(
            "findMovesInRange(jin 10, Startup)",
            "findMovesInRange(jin 10, OnHit)",
            "findMovesInRange(jin 10, OnBlock)",
            "findMovesInRange(jin 10, OnCounter)",
        )
        val fixture = RouterFixture()

        // when
        listOf(Command.Startup, Command.OnHit, Command.OnBlock, Command.OnCounter).forEach { command ->
            fixture.router(command = command, query = "jin 10", source = user)
        }

        // then
        assertThat(fixture.callList).isEqualTo(expected)
    }

    @Test
    fun `stance and strings go to their move lookups`() = runTest {
        // given
        val expected = listOf("findStanceOrMove(jin zen)", "findStrings(jin 1)")
        val fixture = RouterFixture()

        // when
        fixture.router(command = Command.Stance, query = "jin zen", source = user)
        fixture.router(command = Command.Strings, query = "jin 1", source = user)

        // then
        assertThat(fixture.callList).isEqualTo(expected)
    }

    @Test
    fun `char requires character properties`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        fixture.router(command = Command.Char, query = "sol", source = user)

        // then
        assertThat(fixture.callList).containsExactly("findCharacter(sol, true)")
    }

    @Test
    fun `alias finds the game's aliases`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        fixture.router(command = Command.Alias, query = "Tekken_8", source = user)

        // then
        assertThat(fixture.callList).containsExactly("findAliases(Tekken_8)")
    }

    @Test
    fun `core commands create their core responses`() = runTest {
        // given
        val expected = listOf(
            "createTipResponse()",
            "createTipResponse()",
            "createHelpResponse()",
            "createCommandsResponse()",
            "createRepoResponse()",
            "createInviteResponse()",
            "createModulesResponse()",
        )
        val commandList = listOf(
            Command.Tip,
            Command.Donate,
            Command.Help,
            Command.Commands,
            Command.Repo,
            Command.Invite,
            Command.Modules,
        )
        val fixture = RouterFixture()

        // when
        commandList.forEach { command -> fixture.router(command = command, query = "", source = user) }

        // then
        assertThat(fixture.callList).isEqualTo(expected)
    }

    @Test
    fun `source-bound commands pass the source along`() = runTest {
        // given
        val expected = listOf(
            "createSteamLobbyResponse($STEAM_LOBBY_URL, $user)",
            "ban($OFFENDER_HANDLE, $user)",
            "unban($OFFENDER_HANDLE, $user)",
            "forwardFeedback(Heat Smash is off by one, $user)",
            "replyToFeedback($OFFENDER_HANDLE thanks, $user)",
            "refreshWiki($user)",
            "performOperation(register 2Aa4bQ7nJyRf, $user)",
        )
        val fixture = RouterFixture()

        // when
        fixture.router(command = Command.Join, query = STEAM_LOBBY_URL, source = user)
        fixture.router(command = Command.Ban, query = OFFENDER_HANDLE, source = user)
        fixture.router(command = Command.Unban, query = OFFENDER_HANDLE, source = user)
        fixture.router(command = Command.Feedback, query = "Heat Smash is off by one", source = user)
        fixture.router(command = Command.Reply, query = "$OFFENDER_HANDLE thanks", source = user)
        fixture.router(command = Command.Refresh, query = "", source = user)
        fixture.router(command = Command.Ewgf, query = "register 2Aa4bQ7nJyRf", source = user)

        // then
        assertThat(fixture.callList).isEqualTo(expected)
    }

    @Test
    fun `gl finds the glossary term`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        fixture.router(command = Command.Gl, query = "okizeme", source = user)

        // then
        assertThat(fixture.callList).containsExactly("findTerm(okizeme)")
    }

    @Test
    fun `unimplemented commands are errors`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        val resultList = listOf(Command.Banlist, Command.ThrowTK, Command.SpecialROA).map { command ->
            fixture.router(command = command, query = "jin", source = user)
        }

        // then
        resultList.forEach { result ->
            val error = (result as Result.Error).error
            assertThat(error).isInstanceOf(BotError.NotImplemented::class)
        }
    }

    @Test
    fun `unimplemented commands reach no service`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        listOf(Command.Banlist, Command.ThrowTK, Command.SpecialROA).forEach { command ->
            fixture.router(command = command, query = "jin", source = user)
        }

        // then
        assertThat(fixture.callList).isEmpty()
    }
    //endregion

    //region user request
    @Test
    fun `request without a command is frame data`() = runTest {
        // given
        val fixture = RouterFixture(moveService = FakeMoveService(frameDataResult = Result.Success(df1)))

        // when
        fixture.router(UserRequest(command = null, query = "jin df+1", source = user))

        // then
        assertThat(fixture.callList).containsExactly("findFrameData(jin df+1)")
    }

    @Test
    fun `steam lobby url is a join regardless of the command`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        fixture.router(UserRequest(command = Command.Fd, query = STEAM_LOBBY_URL, source = user))

        // then
        assertThat(fixture.callList).containsExactly("createSteamLobbyResponse($STEAM_LOBBY_URL, $user)")
    }

    @Test
    fun `failed request without a command retries with a command found in the query`() = runTest {
        // given
        val expected = listOf("findFrameData(jin heat)", "findMovesOfType(jin, ${MoveType.HEAT})")
        val fixture = RouterFixture()

        // when
        fixture.router(UserRequest(command = null, query = "jin heat", source = user))

        // then
        assertThat(fixture.callList).isEqualTo(expected)
    }

    @Test
    fun `retry returns the retried command's result`() = runTest {
        // given
        val expected = Result.Success(heatListResponse)
        val fixture = RouterFixture(moveService = FakeMoveService(listResult = expected))

        // when
        val result = fixture.router(UserRequest(command = null, query = "jin heat", source = user))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed request without a command word keeps its error`() = runTest {
        // given
        val expected = Result.Error(BotError.UnknownMove("jin", "d+5"))
        val fixture = RouterFixture(moveService = FakeMoveService(frameDataResult = expected))

        // when
        val result = fixture.router(UserRequest(command = null, query = "jin d+5", source = user))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed request with an explicit command isn't retried`() = runTest {
        // given
        val fixture = RouterFixture()

        // when
        fixture.router(UserRequest(command = Command.Fd, query = "jin heat", source = user))

        // then
        assertThat(fixture.callList).containsExactly("findFrameData(jin heat)")
    }

    @Test
    fun `success is recorded with its command and game`() = runTest {
        // given
        val expected = (Command.Fd to Game.Tekken8)
        val statsPort = FakeStatsPort()
        val fixture = RouterFixture(
            moveService = FakeMoveService(frameDataResult = Result.Success(df1)),
            statsPort = statsPort,
        )

        // when
        fixture.router(UserRequest(command = null, query = "jin df+1", source = user))

        // then
        assertThat(statsPort.registeredList).containsExactly(expected)
    }

    @Test
    fun `retried success is recorded with the retried command`() = runTest {
        // given
        val expected = (Command.Heat to Game.Tekken8)
        val statsPort = FakeStatsPort()
        val fixture = RouterFixture(
            moveService = FakeMoveService(listResult = Result.Success(heatListResponse)),
            statsPort = statsPort,
        )

        // when
        fixture.router(UserRequest(command = null, query = "jin heat", source = user))

        // then
        assertThat(statsPort.registeredList).containsExactly(expected)
    }

    @Test
    fun `failure is recorded once`() = runTest {
        // given
        val statsPort = FakeStatsPort()
        val fixture = RouterFixture(statsPort = statsPort)

        // when
        fixture.router(UserRequest(command = null, query = "jin heat", source = user))

        // then
        assertThat(statsPort.failureCount).isEqualTo(1)
    }
    //endregion
}


private const val STEAM_LOBBY_URL = "steam://joinlobby/586140/109775241137042824/76561198443042808"
private const val OFFENDER_HANDLE = "offender-444444444444444444-555555555555555555"
private val user = UserRequest.Source(username = "user", id = "333333333333333333", channelId = "555555555555555555")
private val df1 = moveResponse(input = "df+1", moveName = "Mid check")
private val heatListResponse = ListResponse(
    game = Game.Tekken8,
    title = "JIN Heat moves",
    values = listOf("df+1"),
    dataSource = wavuDataSource,
)
