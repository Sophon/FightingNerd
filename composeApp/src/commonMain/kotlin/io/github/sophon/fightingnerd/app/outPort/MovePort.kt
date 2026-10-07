package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.wiki.model.Move
import kotlinx.coroutines.flow.Flow

internal interface MovePort {
    //TODO: refactor to compose models
    fun subscribeToMoves(game: Game, characterId: String): Flow<List<Move>>
}
