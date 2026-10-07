package io.github.sophon.fightingnerd.feat.scheduler

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.data.PreferenceRepo
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.SchedulerPort
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

internal class WorkManagerScheduler(
    private val context: Context,
    private val preferenceRepo: PreferenceRepo,
) : SchedulerPort {

    override suspend fun setPeriod(duration: Duration): EmptyResult<AppError> {
        val result = try {
            val request = PeriodicWorkRequestBuilder<FightingNerdRefreshWorker>(
                duration.inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
            ).build()

            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request,
                )

            preferenceRepo.setUpdateInterval(duration)
        } catch (e: Exception) {
            Result.Error(AppError.Unknown(e.message.orEmpty()))
        }

        return result
    }

    override suspend fun cancel(): EmptyResult<AppError> {
        val result = try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
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


    private companion object {
        const val WORK_NAME = "fightingnerd_refresh"
    }
}
