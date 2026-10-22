package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.AdminListPort
import io.github.sophon.app.outPort.BanPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class BanUserServiceTest {
    @Test
    fun `non-admin can't ban`() = runTest {
        // given
        val expected = Result.Error(AdminError.PermissionDenied)
        val service = banUserService()

        // when
        val result = service.invoke(banRequest(issuerId = USER_ID))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `non-admin ban isn't stored`() = runTest {
        // given
        val banPort = FakeBanPort()
        val service = banUserService(banPort = banPort)

        // when
        service.invoke(banRequest(issuerId = USER_ID))

        // then
        assertThat(banPort.banMap).isEmpty()
    }

    @Test
    fun `ban starts now and lasts the requested duration`() = runTest {
        // given
        val expected = Result.Success(
            Ban(
                offenderId = OFFENDER_ID,
                bannedAt = now,
                expiresAt = (now + 7.days),
                issuerId = ADMIN_ID,
                preventBotUsage = true,
            ),
        )
        val service = banUserService()

        // when
        val result = service.invoke(banRequest(preventBotUsage = true, duration = 7.days))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `ban is stored under the offender`() = runTest {
        // given
        val expected = mapOf(
            OFFENDER_ID to Ban(
                offenderId = OFFENDER_ID,
                bannedAt = now,
                expiresAt = (now + 7.days),
                issuerId = ADMIN_ID,
                preventBotUsage = false,
            ),
        )
        val banPort = FakeBanPort()
        val service = banUserService(banPort = banPort)

        // when
        service.invoke(banRequest(duration = 7.days))

        // then
        assertThat(banPort.banMap).isEqualTo(expected)
    }

    @Test
    fun `failed store is a database error`() = runTest {
        // given
        val expected = Result.Error(AdminError.Database(DataError.Local.UNKNOWN))
        val service = banUserService(banPort = FakeBanPort(error = DataError.Local.UNKNOWN))

        // when
        val result = service.invoke(banRequest())

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeAdminListPort(
        private val adminIdList: List<String>,
    ): AdminListPort {
        override fun isAdmin(userId: String): Boolean = (userId in adminIdList)

        override fun save(adminIdList: List<String>): EmptyResult<AdminError> = Result.Success(Unit)
    }

    private class FakeBanPort(
        private val error: DataError? = null,
    ): BanPort {
        val banMap = mutableMapOf<String, Ban>()

        override suspend fun ban(ban: Ban): EmptyResult<DataError> {
            if (error != null) return Result.Error(error)

            banMap[ban.offenderId] = ban
            return Result.Success(Unit)
        }

        override suspend fun unban(offenderId: String): EmptyResult<DataError> {
            banMap.remove(offenderId)
            return Result.Success(Unit)
        }

        override suspend fun getBan(offenderId: String): Result<Ban?, DataError> = Result.Success(banMap[offenderId])
    }

    private class FixedClock(private val instant: Instant): Clock {
        override fun now(): Instant = instant
    }

    private fun banUserService(banPort: FakeBanPort = FakeBanPort()): BanUserService {
        val service = BanUserService(
            adminListPort = FakeAdminListPort(adminIdList = listOf(ADMIN_ID)),
            banPort = banPort,
            clock = FixedClock(now),
        )
        return service
    }
}

private fun banRequest(
    issuerId: String = ADMIN_ID,
    preventBotUsage: Boolean = false,
    duration: Duration = 30.days,
): BanRequest {
    val banRequest = BanRequest(
        issuerId = issuerId,
        offenderId = OFFENDER_ID,
        preventBotUsage = preventBotUsage,
        duration = duration,
    )
    return banRequest
}


private const val ADMIN_ID = "111111111111111111"
private const val USER_ID = "333333333333333333"
private const val OFFENDER_ID = "444444444444444444"
private val now = Instant.parse("2026-10-06T18:00:00Z")
