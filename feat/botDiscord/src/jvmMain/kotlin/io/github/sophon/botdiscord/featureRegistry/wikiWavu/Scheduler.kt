package io.github.sophon.botdiscord.featureRegistry.wikiWavu

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

class Scheduler(
    private val scope: CoroutineScope,
) {
    fun <T>start(
        period: Duration,
        task: suspend () -> T,
    ): Flow<T> {
        return flow {
            while (true) {
                emit(task.invoke())
                delay(period)
            }
        }
    }
}