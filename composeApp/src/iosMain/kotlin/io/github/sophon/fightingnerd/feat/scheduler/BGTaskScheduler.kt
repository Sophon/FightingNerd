package io.github.sophon.fightingnerd.feat.scheduler

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import io.github.sophon.fightingnerd.core.usecase.RefreshUseCase
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTask
import platform.BackgroundTasks.BGTaskScheduler as PlatformScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.dateWithTimeIntervalSinceNow
import kotlin.time.Duration

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class BGTaskScheduler(
    private val preferenceRepo: PreferenceRepo,
): SchedulerPort {

    override suspend fun setPeriod(duration: Duration): EmptyResult<AppError> {
        val result = try {
            val submitError = submitBGRequest(duration)
            if (submitError != null) {
                Result.Error(AppError.Unknown(submitError))
            } else {
                preferenceRepo.setUpdateInterval(duration)
            }
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }

    override suspend fun cancel(): EmptyResult<AppError> {
        val result = try {
            PlatformScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
            preferenceRepo.setUpdateInterval(null)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }

    override fun subscribeToPeriod(): Flow<Duration?> {
        val flow = preferenceRepo.subscribeToUpdateInterval()
        return flow
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun handleBGTask(task: BGTask) {
    val koin = KoinPlatform.getKoin()
    val refreshUseCase = koin.get<RefreshUseCase>()
    val preferenceRepo = koin.get<PreferenceRepo>()

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val job = scope.launch {
        var success = true
        try {
            refreshUseCase().collect { emission ->
                emission
                    .onSuccess { report -> Napier.i(tag = TAG) { "bgTask: $report" } }
                    .onError { error ->
                        Napier.e(tag = TAG) { "bgTask: $error" }
                        success = false
                    }
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG, throwable = e) { "bgTask crashed" }
            success = false
        } finally {
            val interval = preferenceRepo.subscribeToUpdateInterval().first()
            if (interval != null) {
                submitBGRequest(interval)
            }
            task.setTaskCompletedWithSuccess(success)
        }
    }

    task.expirationHandler = {
        job.cancel()
        task.setTaskCompletedWithSuccess(false)
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun submitBGRequest(duration: Duration): String? {
    val request = BGAppRefreshTaskRequest(identifier = TASK_IDENTIFIER)
    request.earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(
        duration.inWholeSeconds.toDouble()
    )
    val error = memScoped {
        val errorRef = alloc<ObjCObjectVar<NSError?>>()
        val ok = PlatformScheduler.sharedScheduler.submitTaskRequest(request, errorRef.ptr)
        if (ok) {
            null
        } else {
            errorRef.value?.localizedDescription.orEmpty()
        }
    }
    return error
}


private const val TAG = "Scheduler"
internal const val TASK_IDENTIFIER = "io.github.sophon.fightingnerd.refresh"
