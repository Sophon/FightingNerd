package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.TipOption

interface PurchaseTipUseCase {
    suspend operator fun invoke(tipOption: TipOption): EmptyResult<AppError>
}
