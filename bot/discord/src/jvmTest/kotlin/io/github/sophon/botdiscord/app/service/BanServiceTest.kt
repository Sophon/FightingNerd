package io.github.sophon.botdiscord.app.service

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.ModerationRequest
import io.github.sophon.discord.app.model.UserRequest
import io.github.sophon.discord.app.model.response.BanResponse
import io.github.sophon.discord.app.model.response.UnbanResponse
import io.github.sophon.discord.app.outPort.BanPort
import io.github.sophon.discord.app.service.BanServiceImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class BanServiceTest {
    //region ban
    @Test
    fun `ban without a source is a logic error`() = runTest {
        // given
        val service = BanServiceImpl(banPort = FakeBanPort())

        // when
        val result = service.ban(query = OFFENDER_HANDLE, source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `ban with a malformed offender is an invalid query`() = runTest {
        // given
        val service = BanServiceImpl(banPort = FakeBanPort())

        // when
        val result = service.ban(query = "offender", source = admin)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `invalid ban doesn't reach the ban port`() = runTest {
        // given
        val banPort = FakeBanPort()
        val service = BanServiceImpl(banPort = banPort)

        // when
        service.ban(query = "offender", source = admin)

        // then
        assertThat(banPort.banRequestList).isEmpty()
    }

    @Test
    fun `ban is issued by the source against the offender`() = runTest {
        // given
        val expected = ModerationRequest(authorId = ADMIN_ID, offender = offender)
        val banPort = FakeBanPort()
        val service = BanServiceImpl(banPort = banPort)

        // when
        service.ban(query = OFFENDER_HANDLE, source = admin)

        // then
        assertThat(banPort.banRequestList).containsExactly(expected)
    }

    @Test
    fun `ban returns the ban port's response`() = runTest {
        // given
        val expected = Result.Success(banResponse)
        val service = BanServiceImpl(banPort = FakeBanPort(banResult = expected))

        // when
        val result = service.ban(query = OFFENDER_HANDLE, source = admin)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed ban returns the ban port's error`() = runTest {
        // given
        val expected = Result.Error(BotError.PermissionDenied())
        val service = BanServiceImpl(banPort = FakeBanPort(banResult = expected))

        // when
        val result = service.ban(query = OFFENDER_HANDLE, source = admin)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion

    //region unban
    @Test
    fun `unban without a source is a logic error`() = runTest {
        // given
        val service = BanServiceImpl(banPort = FakeBanPort())

        // when
        val result = service.unban(query = OFFENDER_HANDLE, source = null)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.BotLogicError::class)
    }

    @Test
    fun `unban with a malformed offender is an invalid query`() = runTest {
        // given
        val service = BanServiceImpl(banPort = FakeBanPort())

        // when
        val result = service.unban(query = "offender", source = admin)

        // then
        val error = (result as Result.Error).error
        assertThat(error).isInstanceOf(BotError.InvalidQuery::class)
    }

    @Test
    fun `unban is issued by the source for the offender`() = runTest {
        // given
        val expected = ModerationRequest(authorId = ADMIN_ID, offender = offender)
        val banPort = FakeBanPort()
        val service = BanServiceImpl(banPort = banPort)

        // when
        service.unban(query = OFFENDER_HANDLE, source = admin)

        // then
        assertThat(banPort.unbanRequestList).containsExactly(expected)
    }

    @Test
    fun `unban responds with the offender`() = runTest {
        // given
        val expected = Result.Success(UnbanResponse(offender = offender, dataSource = BOT_DATA_SOURCE))
        val service = BanServiceImpl(banPort = FakeBanPort())

        // when
        val result = service.unban(query = OFFENDER_HANDLE, source = admin)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed unban returns the ban port's error`() = runTest {
        // given
        val expected = Result.Error(BotError.PermissionDenied())
        val service = BanServiceImpl(banPort = FakeBanPort(unbanResult = expected))

        // when
        val result = service.unban(query = OFFENDER_HANDLE, source = admin)

        // then
        assertThat(result).isEqualTo(expected)
    }
    //endregion


    private class FakeBanPort(
        private val banResult: Result<BanResponse, BotError> = Result.Success(banResponse),
        private val unbanResult: EmptyResult<BotError> = Result.Success(Unit),
    ): BanPort {
        val banRequestList = mutableListOf<ModerationRequest>()
        val unbanRequestList = mutableListOf<ModerationRequest>()

        override suspend fun ban(moderationRequest: ModerationRequest): Result<BanResponse, BotError> {
            banRequestList += moderationRequest
            return banResult
        }

        override suspend fun unban(moderationRequest: ModerationRequest): EmptyResult<BotError> {
            unbanRequestList += moderationRequest
            return unbanResult
        }

        override suspend fun isBanned(userId: String): Result<Boolean, BotError> = Result.Success(false)
    }
}


private const val ADMIN_ID = "111111111111111111"
private const val OFFENDER_HANDLE = "offender-444444444444444444-555555555555555555"
private val admin = UserRequest.Source(
    username = "admin",
    id = ADMIN_ID,
    channelId = "555555555555555555",
)
private val offender = UserRequest.Source(
    username = "offender",
    id = "444444444444444444",
    channelId = "555555555555555555",
)
private val bannedAt = Instant.parse("2026-10-06T18:00:00Z")
private val banResponse = BanResponse(
    offender = offender,
    bannedAt = bannedAt,
    expiresAt = (bannedAt + 30.days),
    issuerId = ADMIN_ID,
    preventBotUsage = false,
    dataSource = BOT_DATA_SOURCE,
)
