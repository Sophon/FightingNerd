package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError

internal interface FirstLaunchPort {
    suspend fun hasLaunchedBefore(): Result<Boolean, AppError>
    suspend fun markLaunched(): EmptyResult<AppError>
}
