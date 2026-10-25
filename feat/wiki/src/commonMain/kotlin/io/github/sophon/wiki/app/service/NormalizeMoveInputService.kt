package io.github.sophon.wiki.app.service

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.wiki.NormalizeMoveInputUseCase
import io.github.sophon.wiki.app.outPort.NormalizeMoveInputPort
import io.github.sophon.wiki.model.wiki.Game

@ExcludeFromCoverage("plain port call")
internal class NormalizeMoveInputService(
    private val normalizeMoveInputPort: NormalizeMoveInputPort,
) : NormalizeMoveInputUseCase {
    override fun invoke(game: Game, input: String): String {
        val normalized = normalizeMoveInputPort.normalizeMoveInput(game, input)
        return normalized
    }
}
