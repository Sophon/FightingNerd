package io.github.sophon.fightingnerd.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.MoveListData
import kotlinx.coroutines.flow.Flow

/**
 * Downloaded media of the moves point to their local files.
 */
interface SubscribeToMoveListUseCase {
    operator fun invoke(gameId: String, characterId: String): Flow<Result<MoveListData, AppError>>
}
