package io.github.sophon.domain

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.integration.model.Command
import io.github.sophon.integration.model.DailyReport
import io.github.sophon.integration.model.StatsError
import io.github.sophon.integration.StatsTracker
import io.github.sophon.usecase.GetReportsUseCase
import io.github.sophon.usecase.InitRepoUseCase
import io.github.sophon.usecase.RecordUseCase
import io.github.sophon.usecase.ResetCacheUseCase
import io.github.sophon.usecase.SaveTodaysReport
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

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
                .also {
                    cachedStats.clear()
                }
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
