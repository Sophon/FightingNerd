package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.ComposeConfig

internal interface ConfigureWikiPort {
    suspend fun configure(
        composeConfig: ComposeConfig,
        enabledGameIdSet: Set<String>,
    ): EmptyResult<AppError>
}
