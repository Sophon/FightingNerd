package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.Move
import kotlinx.coroutines.flow.Flow

internal interface MovePort {
    fun subscribeToMoves(gameId: String, characterId: String): Flow<List<Move>>
}
