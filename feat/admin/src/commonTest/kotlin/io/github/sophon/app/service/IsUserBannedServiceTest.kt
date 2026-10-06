package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.BanPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class IsUserBannedServiceTest {
    @Test
    fun `user without a ban is not banned`() = runTest {
        // given
        val expected = Result.Success(false)
        val service = isUserBannedService(banPort = FakeBanPort())

        // when
        val result = service.invoke(OFFENDER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `user with an active ban is banned`() = runTest {
        // given
        val expected = Result.Success(true)
        val service = isUserBannedService(banPort = FakeBanPort(ban(expiresAt = (now + 1.hours))))

        // when
        val result = service.invoke(OFFENDER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `user with an expired ban is not banned`() = runTest {
        // given
        val expected = Result.Success(false)
        val service = isUserBannedService(banPort = FakeBanPort(ban(expiresAt = (now - 1.hours))))

        // when
        val result = service.invoke(OFFENDER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `ban expiring right now is no longer active`() = runTest {
        // given
        val expected = Result.Success(false)
        val service = isUserBannedService(banPort = FakeBanPort(ban(expiresAt = now)))

        // when
        val result = service.invoke(OFFENDER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `another user's ban doesn't ban the user`() = runTest {
        // given
        val expected = Result.Success(false)
        val service = isUserBannedService(banPort = FakeBanPort(ban(expiresAt = (now + 1.hours))))

        // when
        val result = service.invoke(USER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `failed lookup is a database error`() = runTest {
        // given
        val expected = Result.Error(AdminError.Database(DataError.Local.UNKNOWN))
        val service = isUserBannedService(banPort = FakeBanPort(error = DataError.Local.UNKNOWN))

        // when
        val result = service.invoke(OFFENDER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeBanPort(
        vararg storedBans: Ban,
        private val error: DataError? = null,
    ): BanPort {
        private val banMap = storedBans.associateBy { it.offenderId }.toMutableMap()

        override suspend fun ban(ban: Ban): EmptyResult<DataError> {
            banMap[ban.offenderId] = ban
            return Result.Success(Unit)
        }

        override suspend fun unban(offenderId: String): EmptyResult<DataError> {
            banMap.remove(offenderId)
            return Result.Success(Unit)
        }

        override suspend fun getBan(offenderId: String): Result<Ban?, DataError> {
            if (error != null) return Result.Error(error)

            return Result.Success(banMap[offenderId])
        }
    }

    private class FixedClock(private val instant: Instant): Clock {
        override fun now(): Instant = instant
    }

    private fun isUserBannedService(banPort: FakeBanPort): IsUserBannedService {
        val service = IsUserBannedService(banPort = banPort, clock = FixedClock(now))
        return service
    }
}

private fun ban(expiresAt: Instant): Ban {
    val ban = Ban(
        offenderId = OFFENDER_ID,
        bannedAt = (expiresAt - 30.days),
        expiresAt = expiresAt,
        issuerId = ADMIN_ID,
        preventBotUsage = false,
    )
    return ban
}


private const val ADMIN_ID = "111111111111111111"
private const val USER_ID = "333333333333333333"
private const val OFFENDER_ID = "444444444444444444"
private val now = Instant.parse("2026-10-06T18:00:00Z")
