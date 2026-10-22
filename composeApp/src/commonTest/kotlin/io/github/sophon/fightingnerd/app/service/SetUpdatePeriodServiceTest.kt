package io.github.sophon.fightingnerd.app.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import io.github.sophon.fightingnerd.app.outPort.UpdatePeriodPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class SetUpdatePeriodServiceTest {
    @Test
    fun `a period schedules the refresh`() = runTest {
        // given
        val schedulerPort = FakeSchedulerPort()
        val service = SetUpdatePeriodService(schedulerPort, FakeUpdatePeriodPort())
        val expected = listOf<Duration?>(12.hours)

        // when
        service(12.hours)

        // then
        assertThat(schedulerPort.scheduledList).isEqualTo(expected)
    }

    @Test
    fun `no period cancels the refresh`() = runTest {
        // given
        val schedulerPort = FakeSchedulerPort()
        val service = SetUpdatePeriodService(schedulerPort, FakeUpdatePeriodPort())
        val expected = listOf<Duration?>(null)

        // when
        service(null)

        // then
        assertThat(schedulerPort.scheduledList).isEqualTo(expected)
    }

    @Test
    fun `a scheduled period is saved`() = runTest {
        // given
        val updatePeriodPort = FakeUpdatePeriodPort()
        val service = SetUpdatePeriodService(FakeSchedulerPort(), updatePeriodPort)
        val expected = listOf<Duration?>(24.hours)

        // when
        service(24.hours)

        // then
        assertThat(updatePeriodPort.savedList).isEqualTo(expected)
    }

    @Test
    fun `a failed schedule isn't saved`() = runTest {
        // given
        val error = AppError.Unknown("BGTaskSchedulerErrorDomain error 1")
        val updatePeriodPort = FakeUpdatePeriodPort()
        val service = SetUpdatePeriodService(FakeSchedulerPort(Result.Error(error)), updatePeriodPort)
        val expected = Result.Error(error)

        // when
        val result = service(24.hours)

        // then
        assertThat(result).isEqualTo(expected)
        assertThat(updatePeriodPort.savedList).isEqualTo(emptyList())
    }

    @Test
    fun `a failed cancel keeps the saved period`() = runTest {
        // given
        val error = AppError.Unknown("WorkManager is not initialized")
        val updatePeriodPort = FakeUpdatePeriodPort()
        val service = SetUpdatePeriodService(FakeSchedulerPort(Result.Error(error)), updatePeriodPort)
        val expected = Result.Error(error)

        // when
        val result = service(null)

        // then
        assertThat(result).isEqualTo(expected)
        assertThat(updatePeriodPort.savedList).isEqualTo(emptyList())
    }


    private class FakeSchedulerPort(
        private val result: EmptyResult<AppError> = Result.Success(Unit),
    ): SchedulerPort {
        val scheduledList = mutableListOf<Duration?>()

        override suspend fun schedule(period: Duration): EmptyResult<AppError> {
            scheduledList.add(period)
            return result
        }

        override suspend fun cancel(): EmptyResult<AppError> {
            scheduledList.add(null)
            return result
        }
    }

    private class FakeUpdatePeriodPort: UpdatePeriodPort {
        val savedList = mutableListOf<Duration?>()

        override fun subscribe(): Flow<Duration?> {
            error("not used")
        }

        override suspend fun save(period: Duration?): EmptyResult<AppError> {
            savedList.add(period)
            return Result.Success(Unit)
        }
    }
}
