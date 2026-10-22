package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.AdminListPort
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import kotlin.test.Test

class ConfigureAdminToolServiceTest {
    @Test
    fun `saves the admin list`() {
        // given
        val expected = listOf(ADMIN_ID, OTHER_ADMIN_ID)
        val adminListPort = FakeAdminListPort()
        val service = ConfigureAdminToolService(adminListPort = adminListPort)

        // when
        val result = service.invoke(expected)

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(adminListPort.adminIdList).isEqualTo(expected)
    }

    @Test
    fun `new admin list replaces the previous one`() {
        // given
        val expected = listOf(OTHER_ADMIN_ID)
        val adminListPort = FakeAdminListPort(adminIdList = listOf(ADMIN_ID))
        val service = ConfigureAdminToolService(adminListPort = adminListPort)

        // when
        service.invoke(expected)

        // then
        assertThat(adminListPort.adminIdList).isEqualTo(expected)
    }

    @Test
    fun `failed save returns the save error`() {
        // given
        val expected = Result.Error(AdminError.Database(DataError.Local.UNKNOWN))
        val service = ConfigureAdminToolService(
            adminListPort = FakeAdminListPort(error = AdminError.Database(DataError.Local.UNKNOWN)),
        )

        // when
        val result = service.invoke(listOf(ADMIN_ID))

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeAdminListPort(
        var adminIdList: List<String> = emptyList(),
        private val error: AdminError? = null,
    ): AdminListPort {
        override fun isAdmin(userId: String): Boolean = (userId in adminIdList)

        override fun save(adminIdList: List<String>): EmptyResult<AdminError> {
            if (error != null) return Result.Error(error)

            this.adminIdList = adminIdList
            return Result.Success(Unit)
        }
    }
}


private const val ADMIN_ID = "111111111111111111"
private const val OTHER_ADMIN_ID = "222222222222222222"
