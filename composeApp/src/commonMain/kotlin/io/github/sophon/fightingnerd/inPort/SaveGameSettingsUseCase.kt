package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

/**
 * Saves [enabledGameIdSet] as the only enabled games. Nothing else runs if the save fails.
 *
 * On a successful save, the data of every newly disabled game is deleted, and every newly enabled game starts
 * downloading in the background - the download outlives the caller.
 */
interface SaveGameSettingsUseCase {
    suspend operator fun invoke(enabledGameIdSet: Set<String>): EmptyResult<AppError>
}
