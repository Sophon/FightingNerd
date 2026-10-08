package io.github.sophon.fightingnerd.adapter.inbound.scheduler

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.fightingnerd.adapter.TASK_IDENTIFIER
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.inPort.RefreshDataUseCase
import io.github.sophon.fightingnerd.inPort.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToUpdatePeriodUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler as PlatformScheduler

internal fun registerBGTask() {
    val registered = PlatformScheduler.sharedScheduler.registerForTaskWithIdentifier(
        identifier = TASK_IDENTIFIER,
        usingQueue = null,
        launchHandler = { task -> task?.let(::handleBGTask) },
    )
    if (registered.not()) {
        Napier.e(tag = TAG) { "BGTask registration failed for $TASK_IDENTIFIER" }
    }
}

private fun handleBGTask(task: BGTask) {
    val koin = KoinPlatform.getKoin()
    val refreshDataUseCase = koin.get<RefreshDataUseCase>()
    val subscribeToUpdatePeriodUseCase = koin.get<SubscribeToUpdatePeriodUseCase>()
    val setUpdatePeriodUseCase = koin.get<SetUpdatePeriodUseCase>()

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val job = scope.launch {
        var success = true
        try {
            refreshDataUseCase().collect { event ->
                when (event) {
                    is RefreshEvent.Failure -> {
                        Napier.e(tag = TAG) { "bgTask: ${event.error}" }
                        success = false
                    }
                    is RefreshEvent.Finished -> Napier.i(tag = TAG) { "bgTask: $event" }
                    is RefreshEvent.Started, is RefreshEvent.Progress -> {}
                }
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG, throwable = e) { "bgTask crashed" }
            success = false
        } finally {
            // iOS tasks are one-shot - queue the next run with the saved period
            val period = subscribeToUpdatePeriodUseCase().first()
            if (period != null) {
                setUpdatePeriodUseCase(period)
                    .onError { error -> Napier.e(tag = TAG) { "bgTask: rescheduling failed - $error" } }
            }
            task.setTaskCompletedWithSuccess(success)
        }
    }

    task.expirationHandler = {
        job.cancel()
        task.setTaskCompletedWithSuccess(false)
    }
}


private const val TAG = "Scheduler"
