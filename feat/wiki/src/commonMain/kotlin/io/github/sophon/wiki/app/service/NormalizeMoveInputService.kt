package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.app.util.cleanMoveInput
import io.github.sophon.wiki.app.util.normalize2dInputs
import io.github.sophon.wiki.app.util.normalizeDreamCancelInput
import io.github.sophon.wiki.app.util.normalizeSuperComboInput
import io.github.sophon.wiki.app.util.normalizeXkoInput
import io.github.sophon.wiki.inPort.NormalizeMoveInputUseCase
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.model.wiki.Wiki

/**
 * Same per-game input normalization that [RefreshDataService] applies to stored moves.
 */
internal class NormalizeMoveInputService : NormalizeMoveInputUseCase {
    override fun invoke(game: Game, input: String): String {
        val normalized = when (game.wiki) {
            Wiki.Wavu -> input.cleanMoveInput()
            Wiki.Mizuumi, Wiki.DustLoop -> input.normalize2dInputs()
            Wiki.SuperCombo -> input.normalizeSuperComboInput()
            Wiki.Xko -> input.normalizeXkoInput()
            Wiki.DreamCancel -> input.normalizeDreamCancelInput()
            Wiki.DragDown -> input
        }
        return normalized
    }
}
