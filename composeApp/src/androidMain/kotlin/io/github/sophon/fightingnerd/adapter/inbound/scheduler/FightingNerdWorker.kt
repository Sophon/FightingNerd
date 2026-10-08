package io.github.sophon.fightingnerd.adapter.inbound.scheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result as WorkResult
import androidx.work.WorkerParameters
import io.github.aakira.napier.Napier
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.inPort.RefreshDataUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

internal class FightingNerdRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {
    private val refreshDataUseCase: RefreshDataUseCase by inject()

    override suspend fun doWork(): WorkResult {
        Napier.i(tag = TAG) { "doWork: refreshing" }
        refreshDataUseCase().collect { event ->
            when (event) {
                is RefreshEvent.Failure -> Napier.e(tag = TAG) { "doWork: ${event.error}" }
                is RefreshEvent.Finished -> Napier.i(tag = TAG) { "doWork: $event" }
                is RefreshEvent.Started, is RefreshEvent.Progress -> {}
            }
        }

        return WorkResult.success()
    }


    private companion object {
        const val TAG = "Scheduler"
    }
}

