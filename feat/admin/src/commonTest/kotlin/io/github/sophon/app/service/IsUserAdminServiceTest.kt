package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.app.outPort.AdminListPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError
import kotlin.test.Test

class IsUserAdminServiceTest {
    @Test
    fun `listed user is admin`() {
        // given
        val expected = Result.Success(true)
        val service = IsUserAdminService(adminListPort = FakeAdminListPort(adminIdList = listOf(ADMIN_ID)))

        // when
        val result = service.invoke(ADMIN_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `unlisted user is not admin`() {
        // given
        val expected = Result.Success(false)
        val service = IsUserAdminService(adminListPort = FakeAdminListPort(adminIdList = listOf(ADMIN_ID)))

        // when
        val result = service.invoke(USER_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `nobody is admin before the admin list is configured`() {
        // given
        val expected = Result.Success(false)
        val service = IsUserAdminService(adminListPort = FakeAdminListPort(adminIdList = emptyList()))

        // when
        val result = service.invoke(ADMIN_ID)

        // then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeAdminListPort(
        private val adminIdList: List<String>,
    ): AdminListPort {
        override fun isAdmin(userId: String): Boolean = (userId in adminIdList)

        override fun save(adminIdList: List<String>): EmptyResult<AdminError> = Result.Success(Unit)
    }
}


private const val ADMIN_ID = "111111111111111111"
private const val USER_ID = "333333333333333333"
