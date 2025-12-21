package io.github.sophon.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.data.BanRepo
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source
import io.github.sophon.domain.model.Ban
import io.github.sophon.usecase.ProcessFeedbackUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal class ProcessFeedbackUseCaseTest {
    private val usecase = ProcessFeedbackUseCase(FakeRepo())
    val adminConfig = Config.AdminConfig(
        administratorIdList = listOf("111"),
        feedbackChannelIdList = listOf(""),
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
    fun `usecase handles regular feedback`() = runTest {
        //given
        val expected = Result.Success(AdminResult(user, ""))

        //when
        val result = usecase.invoke(user, "", adminConfig)

        //then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `usecase handles banned user`() = runTest {
        //when
        val result = usecase.invoke(banned, "", adminConfig)
        val error = (result as Result.Error).error

        //then
        assertThat(result).isInstanceOf(Result.Error::class)
        assertThat(error).isInstanceOf(AdminError.UserBanned::class)
    }

    //region helpers
    private class FakeRepo: BanRepo {
        override suspend fun ban(
            offenderId: String,
            duration: Duration,
            authorId: String,
            preventBotUsage: Boolean,
        ): Result<Ban, AdminError.DatabaseError> {
            TODO("Not yet implemented")
        }

        override suspend fun getBanStatus(offenderId: String): Result<Ban?, AdminError.DatabaseError> {
            val ban = if (offenderId == "333") {
                Ban(
                    offenderId,
                    Instant.DISTANT_PAST,
                    Instant.DISTANT_FUTURE,
                    "111",
                    false,
                )
            } else null
            return Result.Success(ban)
        }

        override suspend fun getBanList(): Result<List<Ban>, AdminError.DatabaseError> {
            return Result.Success(listOf())
        }

        override suspend fun unban(offenderId: String): EmptyResult<AdminError.DatabaseError> {
            return Result.Success(Unit)
        }

        override suspend fun updatePenalty(
            offenderId: String,
            duration: Duration,
            authorId: String,
            preventBotUsage: Boolean,
        ): Result<Ban, AdminError.DatabaseError> {
            val ban = Ban(
                offenderId,
                Instant.DISTANT_PAST,
                Instant.DISTANT_FUTURE,
                "111",
                false,
            )
            return Result.Success(ban)
        }

        override suspend fun cleanExpiredBans(): EmptyResult<AdminError.DatabaseError> {
            return Result.Success(Unit)
        }
    }
    //endregion
}