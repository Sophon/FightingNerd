package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Move

internal interface MoveGroupPort {
    fun loadGroupIdList(gameId: String, moveList: List<Move>): Result<List<String>, AppError>
}
