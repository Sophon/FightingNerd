package io.github.sophon

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.domain.StatsError
import io.github.sophon.domain.model.Command
import io.github.sophon.domain.model.DailyReport
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface StatsTracker {
    fun record(command: Command): EmptyResult<StatsError>
    fun resetCached(): EmptyResult<StatsError>
    fun getDailyReport(): Result<DailyReport, StatsError>
    fun getReports(duration: Duration = 30.days): Result<List<DailyReport>, StatsError>
}


internal class StatsTrackerImpl(
    //
): StatsTracker {
    private val cachedStats = mutableMapOf<Command, Long>()


    override fun record(command: Command): EmptyResult<StatsError> {
        TODO("Not yet implemented")
    }

    override fun resetCached(): EmptyResult<StatsError> {
        TODO("Not yet implemented")
    }

    override fun getDailyReport(): Result<DailyReport, StatsError> {
        TODO("Not yet implemented")
    }

    override fun getReports(duration: Duration): Result<List<DailyReport>, StatsError> {
        TODO("Not yet implemented")
    }
}
