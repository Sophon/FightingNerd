package io.github.sophon.fightingnerd.feat.review.platform

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.core.model.AppError
import platform.StoreKit.SKStoreReviewController

internal class ReviewHandlerImpl : ReviewHandler {
    override suspend fun requestReview(): EmptyResult<AppError> {
        SKStoreReviewController.requestReview()
        return Result.Success(Unit)
    }
}