package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.TipOption
import io.github.sophon.fightingnerd.app.outPort.TipPort
import io.github.sophon.fightingnerd.inPort.PurchaseTipUseCase

@ExcludeFromCoverage("plain port call")
internal class PurchaseTipService(
    private val tipPort: TipPort,
): PurchaseTipUseCase {
    override suspend fun invoke(tipOption: TipOption): EmptyResult<AppError> {
        return tipPort.purchase(tipOption.id)
    }
}
