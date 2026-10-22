package io.github.sophon.fightingnerd.adapter.outbound.scheduler

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.adapter.TASK_IDENTIFIER
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler as PlatformScheduler
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.dateWithTimeIntervalSinceNow
import kotlin.time.Duration

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class BGTaskScheduler: SchedulerPort {

    override suspend fun schedule(period: Duration): EmptyResult<AppError> {
        val result = try {
            val submitError = submitBGRequest(period)
            if (submitError != null) {
                Result.Error(AppError.Unknown(submitError))
            } else {
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }

    override suspend fun cancel(): EmptyResult<AppError> {
        val result = try {
            PlatformScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(TASK_IDENTIFIER)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }

    private fun submitBGRequest(period: Duration): String? {
        val request = BGAppRefreshTaskRequest(identifier = TASK_IDENTIFIER)
        request.earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(
            period.inWholeSeconds.toDouble()
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
}
