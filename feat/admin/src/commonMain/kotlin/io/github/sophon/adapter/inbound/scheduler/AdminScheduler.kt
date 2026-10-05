package io.github.sophon.adapter.inbound.scheduler

import io.github.aakira.napier.Napier
import io.github.sophon.app.port.outbound.ClearExpiredBansPort
import io.github.sophon.core.architecture.onError
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours

internal class AdminScheduler(
    private val clearExpiredBansPort: ClearExpiredBansPort,
) {
    /**
     * Never returns - launch it in the scope that owns the bot's lifecycle; cancelling that scope stops it.
     */
    suspend fun start() {
        while (true) {
            clearExpiredBansPort.clear()
                .onError { error -> Napier.w(tag = TAG) { "clear expired bans: $error" } }
            delay(CLEAR_EXPIRED_BANS_PERIOD)
        }
    }


    private companion object {
        const val TAG = "AdminScheduler"
        val CLEAR_EXPIRED_BANS_PERIOD = 1.hours
    }
}
