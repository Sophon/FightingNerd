package io.github.sophon

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.StatsError
import io.github.sophon.domain.model.Command
import io.github.sophon.domain.model.DailyReport
import io.github.sophon.domain.usecase.SaveTodaysReport
import io.github.sophon.domain.usecase.GetReportsUseCase
import io.github.sophon.domain.usecase.InitRepoUseCase
import io.github.sophon.domain.usecase.RecordUseCase
import io.github.sophon.domain.usecase.ResetCacheUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface StatsTracker {
    suspend fun init(): EmptyResult<StatsError>
    suspend fun record(command: Command): EmptyResult<StatsError>
    suspend fun finalizeDay(recordLength: Duration = 30.days): Result<DailyReport, StatsError>
    suspend fun resetCached(): EmptyResult<StatsError>
    suspend fun getReports(duration: Duration = 30.days): Result<List<DailyReport>, StatsError>
}


internal class StatsTrackerImpl(
    private val initRepoUseCase: InitRepoUseCase,
    private val recordUseCase: RecordUseCase,
    private val saveTodaysReport: SaveTodaysReport,
    private val resetCacheUseCase: ResetCacheUseCase,
    private val getReportsUseCase: GetReportsUseCase,
): StatsTracker {
    private val mutex = Mutex()
    private val cachedStats = mutableMapOf<Command, Long>()


    override suspend fun init(): EmptyResult<StatsError> {
        return initRepoUseCase.invoke()
    }

    override suspend fun record(command: Command): EmptyResult<StatsError> {
        mutex.withLock {
            return recordUseCase.invoke(command, cachedStats)
        }
    }

    override suspend fun finalizeDay(
        recordLength: Duration,
    ): Result<DailyReport, StatsError> {
        mutex.withLock {
            return saveTodaysReport.invoke(recordLength, cachedStats)
                .also { resetCached() }
        }
    }

    override suspend fun resetCached(): EmptyResult<StatsError> {
        mutex.withLock {
            return resetCacheUseCase.invoke(cachedStats)
        }
    }

    override suspend fun getReports(
        duration: Duration,
    ): Result<List<DailyReport>, StatsError> {
        return getReportsUseCase.invoke(duration)
    }
}
