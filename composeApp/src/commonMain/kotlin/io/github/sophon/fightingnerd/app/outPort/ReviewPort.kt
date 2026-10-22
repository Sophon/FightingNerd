package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

internal interface ReviewPort {
    suspend fun requestReview(): EmptyResult<AppError>
}
