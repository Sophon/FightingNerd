package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.TipOption

internal interface TipPort {
    suspend fun getTipOptions(): Result<List<TipOption>, AppError>
    suspend fun purchase(tipOptionId: String): EmptyResult<AppError>
}
