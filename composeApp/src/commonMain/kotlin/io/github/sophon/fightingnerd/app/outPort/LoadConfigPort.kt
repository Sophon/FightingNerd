package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig

internal interface LoadConfigPort {
    suspend fun load(): Result<ComposeConfig, AppError>
}
