package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.TipOption
import io.github.sophon.fightingnerd.app.outPort.TipPort
import io.github.sophon.fightingnerd.inPort.GetTipOptionsUseCase

@ExcludeFromCoverage("plain port call")
internal class GetTipOptionsService(
    private val tipPort: TipPort,
): GetTipOptionsUseCase {
    override suspend fun invoke(): Result<List<TipOption>, AppError> {
        return tipPort.getTipOptions()
    }
}
