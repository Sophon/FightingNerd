package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import kotlin.time.Duration

/**
 * Schedules the background refresh every [period]; `null` turns it off.
 */
interface SetUpdatePeriodUseCase {
    suspend operator fun invoke(period: Duration?): EmptyResult<AppError>
}
