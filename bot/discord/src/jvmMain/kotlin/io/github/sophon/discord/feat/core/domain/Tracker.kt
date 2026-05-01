package io.github.sophon.discord.feat.core.domain

import io.github.aakira.napier.Napier
import io.github.sophon.integration.StatsTracker
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.integration.StatsFeatureInfo
import io.github.sophon.integration.model.DailyReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

private typealias BotCommand = Command
private typealias TrackedCommand = io.github.sophon.integration.model.Command

internal interface Tracker {
    val statsChannelId: String
    fun subscribe(): Flow<DailyReport>
    suspend fun recordSuccessfulCommand(
        featureName: String,
        command: BotCommand,
    ): EmptyResult<BotError>
    suspend fun recordFailure(): EmptyResult<BotError>
}

@OptIn(ExperimentalTime::class)
internal class TrackerImpl(
    statsFeatureInfo: StatsFeatureInfo,
    override val statsChannelId: String,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
    private val statsTracker: StatsTracker,
): Tracker {
    val featureInfo = statsFeatureInfo.featureInfo
    private val reports = MutableSharedFlow<DailyReport>()

    override fun subscribe(): Flow<DailyReport> {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }
        scope.launch {
            statsTracker.init()
                .onError { Napier.e(tag = TAG) { it.toString() } }
        }
        scheduleDaily()
//        scheduleDailyTest()

        return reports.asSharedFlow()
    }


    override suspend fun recordSuccessfulCommand(
        featureName: String,
        command: BotCommand,
    ): EmptyResult<BotError> {
        val trackedCommand = TrackedCommand(
            feature = featureName,
            name = command.name,
        )
        return statsTracker.record(trackedCommand)
            .mapError { it.toDomainError() }
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    override suspend fun recordFailure(): EmptyResult<BotError> {
        val failure = TrackedCommand(
            feature = "failed",
            name = "failed",
        )

        return statsTracker.record(failure)
            .mapError { it.toDomainError() }
    }


    private fun scheduleDaily() {
        val now = Clock.System.now()
        val nextMidnight = now
            .toLocalDateTime(TimeZone.UTC)
            .date
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.UTC)
        val delay = nextMidnight - now

        scheduler.start(
            initialDelay = delay,
            period = 24.hours,
            task = {
                statsTracker.finalizeDay()
                    .mapError { it.toDomainError() }
            }
        ).onEach { result ->
            result
                .onSuccess { dailyReport ->
                    reports.emit(dailyReport)
                }
                .onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    private fun scheduleDailyTest() {
        scheduler.start(
            initialDelay = 1.minutes,
            period = 1.hours,
            task = {
                statsTracker.finalizeDay()
                    .mapError { it.toDomainError() }
            }
        ).onEach { result ->
            result
                .onSuccess { dailyReport ->
                    reports.emit(dailyReport)
                }
                .onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }

    private companion object Companion {
        const val TAG = "StatsDiscordFeature"
    }
}
