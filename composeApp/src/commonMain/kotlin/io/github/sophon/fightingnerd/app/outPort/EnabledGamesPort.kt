package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError

internal interface EnabledGamesPort {
    suspend fun load(): Result<Set<String>, AppError>
}
