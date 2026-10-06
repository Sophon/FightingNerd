package io.github.sophon.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import io.github.sophon.app.outPort.PrepareStoragePort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.StatsError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ConfigureStatsServiceTest {
    @Test
    fun `prepares the storage`() = runTest {
        // given
        val prepareStoragePort = FakePrepareStoragePort()
        val service = ConfigureStatsService(prepareStoragePort = prepareStoragePort)

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Success(Unit))
        assertThat(prepareStoragePort.isPrepared).isTrue()
    }

    @Test
    fun `failed preparation is returned`() = runTest {
        // given
        val error = StatsError.FileError("stats", "permission denied")
        val service = ConfigureStatsService(prepareStoragePort = FakePrepareStoragePort(error = error))

        // when
        val result = service.invoke()

        // then
        assertThat(result).isEqualTo(Result.Error(error))
    }


    private class FakePrepareStoragePort(
        private val error: StatsError? = null,
    ): PrepareStoragePort {
        var isPrepared = false

        override suspend fun prepare(): EmptyResult<StatsError> {
            if (error != null) return Result.Error(error)

            isPrepared = true
            return Result.Success(Unit)
        }
    }
}
