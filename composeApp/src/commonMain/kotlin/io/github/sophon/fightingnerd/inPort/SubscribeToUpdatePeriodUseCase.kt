package io.github.sophon.fightingnerd.inPort

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * The period of the background refresh - `null` if it's off.
 */
interface SubscribeToUpdatePeriodUseCase {
    operator fun invoke(): Flow<Duration?>
}
