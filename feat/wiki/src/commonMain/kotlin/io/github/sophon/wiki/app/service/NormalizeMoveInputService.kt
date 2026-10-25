package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.NormalizeMoveInputUseCase
import io.github.sophon.wiki.app.outPort.NormalizeMoveInputPort
import io.github.sophon.wiki.model.wiki.Game

internal class NormalizeMoveInputService(
    private val normalizeMoveInputPort: NormalizeMoveInputPort,
) : NormalizeMoveInputUseCase {
    override fun invoke(game: Game, input: String): String {
        val normalized = normalizeMoveInputPort.normalizeMoveInput(game, input)
        return normalized
    }
}
