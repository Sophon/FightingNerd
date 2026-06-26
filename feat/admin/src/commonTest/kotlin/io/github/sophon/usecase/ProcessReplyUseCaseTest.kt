package io.github.sophon.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.integration.model.AdminError
import io.github.sophon.integration.model.AdminResult
import io.github.sophon.integration.model.Source
import kotlin.test.Test

internal class ProcessReplyUseCaseTest {
    private val useCase = ProcessReplyUseCase()
    val adminConfig = Config.AdminConfig(
        administratorIdList = listOf("111"),
        feedbackChannelIdList = listOf(""),
        adminServerId = "",
    )
    val admin = Source(
        username = "admin",
        id = "111",
        channelId = "",
    )
    val user = Source(
        username = "user",
        id = "222",
        channelId = "",
    )
    val banned = Source(
        username = "banned",
        id = "333",
        channelId = "",
    )

    @Test
    fun `processReply handles non admin user`() {
        //when
        val result = useCase.invoke(user, admin, "", adminConfig)
        val error = (result as Result.Error).error

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(error).isInstanceOf(AdminError.PermissionDenied::class)
    }

    @Test
    fun `processReply handles admin`() {
        //given
        val expected = Result.Success(AdminResult(user, ""))

        //when
        val result = useCase.invoke(admin, user, "", adminConfig)

        //then
        assertThat(result).isEqualTo(expected)
    }
}