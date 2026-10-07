package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.InstallationPort
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

internal class RecordInstallationServiceTest {

    @Test
    fun `saves current timestamp when no installation timestamp is stored`() = runTest {
        // given
        val installationPort = FakeInstallationPort(Result.Success(null))
        val service = RecordInstallationService(installationPort = installationPort)

        // when
        service()
        val savedTimestamp = installationPort.savedTimestamp

        //then
        assertThat(savedTimestamp).isNotNull()
    }

    @Test
    fun `does not overwrite an existing installation timestamp`() = runTest {
        // given
        val existing = Clock.System.now() - 3.days
        val installationPort = FakeInstallationPort(Result.Success(existing))
        val service = RecordInstallationService(installationPort = installationPort)
        val expected: Instant? = null

        // when
        service()
        val savedTimestamp = installationPort.savedTimestamp

        //then
        assertThat(savedTimestamp).isEqualTo(expected)
    }

    @Test
    fun `does not overwrite an unreadable installation timestamp`() = runTest {
        // given
        val error = AppError.IOError("corrupted preferences")
        val installationPort = FakeInstallationPort(Result.Error(error))
        val service = RecordInstallationService(installationPort = installationPort)
        val expected = Result.Error(error)

        // when
        val result = service()

        //then
        assertThat(result).isEqualTo(expected)
    }


    private class FakeInstallationPort(
        private val timestampResult: Result<Instant?, AppError>,
    ): InstallationPort {
        var savedTimestamp: Instant? = null
            private set

        override suspend fun getInstallationTimestamp(): Result<Instant?, AppError> {
            return timestampResult
        }

        override suspend fun saveInstallationTimestamp(timestamp: Instant): EmptyResult<AppError> {
            savedTimestamp = timestamp
            return Result.Success(Unit)
        }
    }
}
