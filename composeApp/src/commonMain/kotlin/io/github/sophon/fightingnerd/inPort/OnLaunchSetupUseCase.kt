package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

/**
 * Returns once the wiki is configured. Succeeds with the refresh of the default games on the first launch,
 * an empty flow otherwise. The refresh is cold - the games are only downloaded while it's collected.
 */
interface OnLaunchSetupUseCase {
    suspend operator fun invoke(): Result<Flow<RefreshEvent>, AppError>
}
