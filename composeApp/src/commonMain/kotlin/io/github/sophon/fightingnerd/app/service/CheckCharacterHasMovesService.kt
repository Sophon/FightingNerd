package io.github.sophon.fightingnerd.app.service

import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.inPort.CheckCharacterHasMovesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class CheckCharacterHasMovesService(
    private val movePort: MovePort,
): CheckCharacterHasMovesUseCase {
    override fun invoke(
        game: Game,
        characterId: String,
    ): Flow<Boolean> {
        val flow = movePort.subscribeToMoves(game, characterId)
            .map { moveList -> moveList.isNotEmpty() }
            .distinctUntilChanged()
        return flow
    }
}
