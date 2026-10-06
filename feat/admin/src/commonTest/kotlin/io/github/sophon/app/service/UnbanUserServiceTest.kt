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
import io.github.sophon.model.UnbanRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

class UnbanUserServiceTest {
    @Test
    fun `non-admin can't unban`() = runTest {
        // given
        val expected = Result.Error(AdminError.PermissionDenied)
        val service = unbanUserService(banPort = FakeBanPort(activeBan))

        // when
        val result = service.invoke(UnbanRequest(issuerId = USER_ID, offenderId = OFFENDER_ID))

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `non-admin unban keeps the ban`() = runTest {
        // given
        val expected = mapOf(OFFENDER_ID to activeBan)
        val banPort = FakeBanPort(activeBan)
        val service = unbanUserService(banPort = banPort)

        // when
        service.invoke(UnbanRequest(issuerId = USER_ID, offenderId = OFFENDER_ID))

        // then
        assertThat(banPort.banMap).isEqualTo(expected)
    }

    @Test
    fun `unban removes the ban`() = runTest {
        // given
        val banPort = FakeBanPort(activeBan)
        val service = unbanUserService(banPort = banPort)

        // when
        val result = service.invoke(UnbanRequest(issuerId = ADMIN_ID, offenderId = OFFENDER_ID))

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(banPort.banMap).isEmpty()
    }

    @Test
    fun `failed unban is a database error`() = runTest {
        // given
        val expected = Result.Error(AdminError.Database(DataError.Local.UNKNOWN))
        val service = unbanUserService(banPort = FakeBanPort(activeBan, error = DataError.Local.UNKNOWN))

        // when
        val result = service.invoke(UnbanRequest(issuerId = ADMIN_ID, offenderId = OFFENDER_ID))

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
        vararg storedBans: Ban,
        private val error: DataError? = null,
    ): BanPort {
        val banMap = storedBans.associateBy { it.offenderId }.toMutableMap()

        override suspend fun ban(ban: Ban): EmptyResult<DataError> {
            banMap[ban.offenderId] = ban
            return Result.Success(Unit)
        }

        override suspend fun unban(offenderId: String): EmptyResult<DataError> {
            if (error != null) return Result.Error(error)

            banMap.remove(offenderId)
            return Result.Success(Unit)
        }

        override suspend fun getBan(offenderId: String): Result<Ban?, DataError> = Result.Success(banMap[offenderId])
    }

    private fun unbanUserService(banPort: FakeBanPort): UnbanUserService {
        val service = UnbanUserService(
            adminListPort = FakeAdminListPort(adminIdList = listOf(ADMIN_ID)),
            banPort = banPort,
        )
        return service
    }
}


private const val ADMIN_ID = "111111111111111111"
private const val USER_ID = "333333333333333333"
private const val OFFENDER_ID = "444444444444444444"
private val bannedAt = Instant.parse("2026-10-06T18:00:00Z")
private val activeBan = Ban(
    offenderId = OFFENDER_ID,
    bannedAt = bannedAt,
    expiresAt = (bannedAt + 30.days),
    issuerId = ADMIN_ID,
    preventBotUsage = false,
)
