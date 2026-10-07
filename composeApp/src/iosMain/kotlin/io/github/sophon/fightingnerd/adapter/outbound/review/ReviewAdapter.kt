package io.github.sophon.fightingnerd.adapter.outbound.review

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.ReviewPort
import platform.StoreKit.SKStoreReviewController

internal class ReviewAdapter : ReviewPort {
    override suspend fun requestReview(): EmptyResult<AppError> {
        SKStoreReviewController.requestReview()
        return Result.Success(Unit)
    }
}