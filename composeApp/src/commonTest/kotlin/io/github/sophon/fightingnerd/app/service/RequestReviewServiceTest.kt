package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.SessionContext
import io.github.sophon.fightingnerd.app.outPort.InstallationPort
import io.github.sophon.fightingnerd.app.outPort.ReviewPort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class RequestReviewServiceTest {
    private val reviewPort = FakeReviewPort()

    @Test
    fun `requests review when session is long enough and install is old enough`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(Clock.System.now() - 30.days))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 1

        // when
        service(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }

    @Test
    fun `skips review when session duration is below the threshold`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(Clock.System.now() - 30.days))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 0

        // when
        service(SessionContext.MoveList(duration = 5.seconds))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }

    @Test
    fun `skips review when no installation timestamp is stored`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(null))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 0

        // when
        service(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }

    @Test
    fun `skips review when installation timestamp can't be read`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Error(AppError.IOError("corrupted preferences")))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 0

        // when
        service(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }

    @Test
    fun `skips review when installation age is below the threshold`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(Clock.System.now() - 1.days))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 0

        // when
        service(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }

    @Test
    fun `requests review for Quiz session when correct answer pct meets the threshold`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(Clock.System.now() - 30.days))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 1

        // when
        service(SessionContext.Quiz(duration = 15.seconds, correctAnswerPct = 90))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }

    @Test
    fun `skips review for Quiz session when correct answer pct is below the threshold`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(Clock.System.now() - 30.days))
        val service = RequestReviewService(
            installationPort = installationPort,
            reviewPort = reviewPort,
            appScope = this,
        )
        val expected = 0

        // when
        service(SessionContext.Quiz(duration = 15.seconds, correctAnswerPct = 50))
        advanceUntilIdle()
        val requestCount = reviewPort.requestCount

        //then
        assertThat(requestCount).isEqualTo(expected)
    }


    private class FakeInstallationPort(
        private val timestampResult: Result<Instant?, AppError>,
    ): InstallationPort {
        override suspend fun getInstallationTimestamp(): Result<Instant?, AppError> {
            return timestampResult
        }

        override suspend fun saveInstallationTimestamp(timestamp: Instant): EmptyResult<AppError> {
            return Result.Success(Unit)
        }
    }

    private class FakeReviewPort: ReviewPort {
        var requestCount = 0
            private set

        override suspend fun requestReview(): EmptyResult<AppError> {
            requestCount++
            return Result.Success(Unit)
        }
    }
}
