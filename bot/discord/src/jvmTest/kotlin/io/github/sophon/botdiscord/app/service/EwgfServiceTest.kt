package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.EwgfOperation
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.EwgfResponse
import io.github.sophon.discord.app.outPort.EwgfPort
import io.github.sophon.discord.app.service.EwgfServiceImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class EwgfServiceTest {
    @Test
    fun `operation without a source is a logic error`() = runTest {
        // given
        val service = EwgfServiceImpl(ewgfPort = FakeEwgfPort())

        // when
        val result = service.performOperation(query = "", source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `malformed query is a syntax error`() = runTest {
        // given
        val service = EwgfServiceImpl(ewgfPort = FakeEwgfPort())

        // when
        val result = service.performOperation(query = "regster $POLARIS_ID", source = user)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.SyntaxError::class)
    }

    @Test
    fun `help responds with the ewgf data source`() = runTest {
        // given
        val expected = Result.Success(EwgfResponse.Help(dataSource = ewgfDataSource))
        val service = EwgfServiceImpl(ewgfPort = FakeEwgfPort())

        // when
        val result = service.performOperation(query = "help", source = user)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `blank query fetches the source's recent sets`() = runTest {
        // given
        val ewgfPort = FakeEwgfPort()
        val service = EwgfServiceImpl(ewgfPort = ewgfPort)

        // when
        service.performOperation(query = "", source = user)

        // then
        assertThat(ewgfPort.callList).containsExactly("getRecentSets($USER_ID)")
    }

    @Test
    fun `recent sets are returned`() = runTest {
        // given
        val expected = Result.Success(recentSets)
        val service = EwgfServiceImpl(ewgfPort = FakeEwgfPort())

        // when
        val result = service.performOperation(query = "", source = user)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `mention fetches the mentioned user's recent sets`() = runTest {
        // given
        val ewgfPort = FakeEwgfPort()
        val service = EwgfServiceImpl(ewgfPort = ewgfPort)

        // when
        service.performOperation(query = "<@$MENTIONED_ID>", source = user)

        // then
        assertThat(ewgfPort.callList).containsExactly("getRecentSets($MENTIONED_ID)")
    }

    @Test
    fun `register links the source to the polaris id`() = runTest {
        // given
        val ewgfPort = FakeEwgfPort()
        val service = EwgfServiceImpl(ewgfPort = ewgfPort)

        // when
        service.performOperation(query = "register $POLARIS_ID", source = user)

        // then
        assertThat(ewgfPort.callList).containsExactly("register($USER_ID, $POLARIS_ID)")
    }

    @Test
    fun `successful register responds with the operation`() = runTest {
        // given
        val expected = Result.Success(
            EwgfResponse.Success(dataSource = ewgfDataSource, operation = EwgfOperation.Register(POLARIS_ID)),
        )
        val service = EwgfServiceImpl(ewgfPort = FakeEwgfPort())

        // when
        val result = service.performOperation(query = "register $POLARIS_ID", source = user)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `update changes the source's polaris id`() = runTest {
        // given
        val ewgfPort = FakeEwgfPort()
        val service = EwgfServiceImpl(ewgfPort = ewgfPort)

        // when
        service.performOperation(query = "update $POLARIS_ID", source = user)

        // then
        assertThat(ewgfPort.callList).containsExactly("updatePolarisId($USER_ID, $POLARIS_ID)")
    }

    @Test
    fun `unregister removes the source`() = runTest {
        // given
        val ewgfPort = FakeEwgfPort()
        val service = EwgfServiceImpl(ewgfPort = ewgfPort)

        // when
        service.performOperation(query = "unregister", source = user)

        // then
        assertThat(ewgfPort.callList).containsExactly("unregister($USER_ID)")
    }

    @Test
    fun `failed operation returns the port's error`() = runTest {
        // given
        val expected = Result.Error(BotError.PlayerNotRegistered())
        val service = EwgfServiceImpl(ewgfPort = FakeEwgfPort(emptyResult = expected))

        // when
        val result = service.performOperation(query = "unregister", source = user)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `help doesn't reach the port`() = runTest {
        // given
        val ewgfPort = FakeEwgfPort()
        val service = EwgfServiceImpl(ewgfPort = ewgfPort)

        // when
        service.performOperation(query = "help", source = user)

        // then
        assertThat(ewgfPort.callList).isEmpty()
    }


    private class FakeEwgfPort(
        private val emptyResult: EmptyResult<BotError> = Result.Success(Unit),
    ): EwgfPort {
        val callList = mutableListOf<String>()

        override val dataSource: BotResponse.DataSource = ewgfDataSource

        override suspend fun getRecentSets(discordId: String): Result<EwgfResponse.RecentSets, BotError> {
            callList += "getRecentSets($discordId)"
            return Result.Success(recentSets)
        }

        override suspend fun register(discordId: String, polarisId: String): EmptyResult<BotError> {
            callList += "register($discordId, $polarisId)"
            return emptyResult
        }

        override suspend fun updatePolarisId(discordId: String, polarisId: String): EmptyResult<BotError> {
            callList += "updatePolarisId($discordId, $polarisId)"
            return emptyResult
        }

        override suspend fun unregister(discordId: String): EmptyResult<BotError> {
            callList += "unregister($discordId)"
            return emptyResult
        }
    }
}


private const val USER_ID = "333333333333333333"
private const val MENTIONED_ID = "786351781168939038"
private const val POLARIS_ID = "2Aa4bQ7nJyRf"
private val user = UserRequest.Source(username = "user", id = USER_ID, channelId = "555555555555555555")
private val ewgfDataSource = BotResponse.DataSource(
    name = "EWGF",
    iconUrl = "https://ewgf.gg/favicon.png",
    color = 0x9F5FF7,
)
private val recentSets = EwgfResponse.RecentSets(
    dataSource = ewgfDataSource,
    playerName = "Arslan Ash",
    playerRank = "God of Destruction",
    profileUrl = "https://ewgf.gg/player/$POLARIS_ID",
    setList = emptyList(),
)
