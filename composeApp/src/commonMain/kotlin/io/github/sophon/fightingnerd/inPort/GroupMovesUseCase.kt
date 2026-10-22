package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.GroupedMoveList
import io.github.sophon.fightingnerd.app.model.Move

/**
 * Orders the moves by the game's groups, with a bookmark at the start of every non-empty group.
 */
interface GroupMovesUseCase {
    operator fun invoke(gameId: String, moveList: List<Move>): Result<GroupedMoveList, AppError>
}
