package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Release

internal interface ReleasePort {
    suspend fun getReleases(): Result<List<Release>, AppError>
}
