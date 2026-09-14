package io.github.sophon.fightingnerd.core.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.github.sophon.fightingnerd.feat.FakeReviewPolicyRepo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

internal class RecordInstallationUseCaseTest {

    @Test
    fun `usecase saves current timestamp when no installation timestamp is stored`() = runTest {
        // given
        val repo = FakeReviewPolicyRepo(initialTimestamp = null)
        val usecase = RecordInstallationUseCase(repo = repo)

        // when
        usecase()
        val savedTimestamp = repo.savedTimestamp

        //then
        assertThat(savedTimestamp).isNotNull()
    }

    @Test
    fun `usecase does not overwrite an existing installation timestamp`() = runTest {
        // given
        val existing = Clock.System.now() - 3.days
        val repo = FakeReviewPolicyRepo(initialTimestamp = existing)
        val usecase = RecordInstallationUseCase(repo = repo)
        val expectedSaved: Instant? = null

        // when
        usecase()
        val savedTimestamp = repo.savedTimestamp

        //then
        assertThat(savedTimestamp).isEqualTo(expectedSaved)
    }
}
