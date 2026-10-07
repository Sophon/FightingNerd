package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Game

internal interface SaveGameSettingsPort {
    suspend fun saveGameSettings(isEnabledByGame: Map<Game, Boolean>): EmptyResult<AppError>
}
