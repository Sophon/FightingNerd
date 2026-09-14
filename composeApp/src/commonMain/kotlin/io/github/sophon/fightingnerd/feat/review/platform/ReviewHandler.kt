package io.github.sophon.fightingnerd.feat.review.platform

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.core.model.AppError

internal interface ReviewHandler {
    suspend fun requestReview(): EmptyResult<AppError>
}
