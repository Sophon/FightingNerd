package io.github.sophon.fightingnerd.core.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.fightingnerd.feat.FakeReviewHandler
import io.github.sophon.fightingnerd.feat.FakeReviewPolicyRepo
import io.github.sophon.fightingnerd.feat.review.SessionContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class RequestReviewUseCaseTest {

    @Test
    fun `usecase requests review when session is long enough and install is old enough`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = Clock.System.now() - 30.days)
        val handler = FakeReviewHandler()
        val usecase = RequestReviewUseCase(
            reviewPolicyRepo = repo,
            reviewHandler = handler,
            appScope = backgroundScope,
        )
        val expectedRequestCount = 1

        // when
        usecase(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = handler.requestCount

        //then
        assertThat(requestCount).isEqualTo(expectedRequestCount)
    }

    @Test
    fun `usecase skips review when session duration is below the threshold`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = Clock.System.now() - 30.days)
        val handler = FakeReviewHandler()
        val usecase = RequestReviewUseCase(
            reviewPolicyRepo = repo,
            reviewHandler = handler,
            appScope = backgroundScope,
        )
        val expectedRequestCount = 0

        // when
        usecase(SessionContext.MoveList(duration = 5.seconds))
        advanceUntilIdle()
        val requestCount = handler.requestCount

        //then
        assertThat(requestCount).isEqualTo(expectedRequestCount)
    }

    @Test
    fun `usecase skips review when no installation timestamp is stored`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = null)
        val handler = FakeReviewHandler()
        val usecase = RequestReviewUseCase(
            reviewPolicyRepo = repo,
            reviewHandler = handler,
            appScope = backgroundScope,
        )
        val expectedRequestCount = 0

        // when
        usecase(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = handler.requestCount

        //then
        assertThat(requestCount).isEqualTo(expectedRequestCount)
    }

    @Test
    fun `usecase skips review when installation age is below the threshold`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = Clock.System.now() - 1.days)
        val handler = FakeReviewHandler()
        val usecase = RequestReviewUseCase(
            reviewPolicyRepo = repo,
            reviewHandler = handler,
            appScope = backgroundScope,
        )
        val expectedRequestCount = 0

        // when
        usecase(SessionContext.MoveList(duration = 15.seconds))
        advanceUntilIdle()
        val requestCount = handler.requestCount

        //then
        assertThat(requestCount).isEqualTo(expectedRequestCount)
    }

    @Test
    fun `usecase requests review for Quiz session when correct answer pct meets the threshold`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = Clock.System.now() - 30.days)
        val handler = FakeReviewHandler()
        val usecase = RequestReviewUseCase(
            reviewPolicyRepo = repo,
            reviewHandler = handler,
            appScope = backgroundScope,
        )
        val expectedRequestCount = 1

        // when
        usecase(SessionContext.Quiz(duration = 15.seconds, correctAnswerPct = 90))
        advanceUntilIdle()
        val requestCount = handler.requestCount

        //then
        assertThat(requestCount).isEqualTo(expectedRequestCount)
    }

    @Test
    fun `usecase skips review for Quiz session when correct answer pct is below the threshold`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = Clock.System.now() - 30.days)
        val handler = FakeReviewHandler()
        val usecase = RequestReviewUseCase(
            reviewPolicyRepo = repo,
            reviewHandler = handler,
            appScope = backgroundScope,
        )
        val expectedRequestCount = 0

        // when
        usecase(SessionContext.Quiz(duration = 15.seconds, correctAnswerPct = 50))
        advanceUntilIdle()
        val requestCount = handler.requestCount

        //then
        assertThat(requestCount).isEqualTo(expectedRequestCount)
    }
}
