package io.github.sophon.discord.domain

import io.github.aakira.napier.Napier
import io.github.sophon.StatsTracker
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.model.Command
import io.github.sophon.domain.StatsFeatureInfo
import io.github.sophon.domain.model.DailyReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private typealias BotCommand = Command
private typealias TrackedCommand = io.github.sophon.domain.model.Command

internal class Tracker(
    statsFeatureInfo: StatsFeatureInfo,
    private val scheduler: Scheduler,
    private val scope: CoroutineScope,
    private val statsTracker: StatsTracker,
) {
    val featureInfo = statsFeatureInfo.featureInfo
    private val reports = MutableSharedFlow<DailyReport>()

    fun subscribe(): Flow<DailyReport> {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }
        scope.launch {
            statsTracker.init()
                .onError { Napier.e(tag = TAG) { it.toString() } }
        }
        scheduleDaily()

        return reports.asSharedFlow()
    }


    suspend fun recordSuccessfulCommand(
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


    private fun scheduleDaily() {
        scheduler.start(
            task = {
                statsTracker.finalizeDay()
                    .mapError { it.toDomainError() }
            }
        ).onEach { result ->
            result
                .onSuccess { dailyReport ->
                    reports.tryEmit(dailyReport)
                }
                .onError { Napier.e(tag = TAG) { it.toString() } }
        }.launchIn(scope)
    }


    private companion object Companion {
        const val TAG = "StatsDiscordFeature"
    }
}
