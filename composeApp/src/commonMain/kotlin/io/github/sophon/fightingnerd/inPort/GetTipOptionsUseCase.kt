package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.TipOption

interface GetTipOptionsUseCase {
    suspend operator fun invoke(): Result<List<TipOption>, AppError>
}
