package io.github.sophon.fightingnerd.adapter.outbound.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.adapter.inbound.scheduler.FightingNerdRefreshWorker
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

internal class WorkManagerScheduler(
    private val context: Context,
) : SchedulerPort {

    override suspend fun schedule(period: Duration): EmptyResult<AppError> {
        val result = try {
            val request = PeriodicWorkRequestBuilder<FightingNerdRefreshWorker>(
                period.inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
            ).build()

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request,
                )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }

        return result
    }

    override suspend fun cancel(): EmptyResult<AppError> {
        val result = try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }
        return result
    }


    private companion object {
        const val WORK_NAME = "fightingnerd_refresh"
    }
}
