package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.Move
import kotlinx.coroutines.flow.Flow

/**
 * Emits the running count of downloaded media after every move.
 */
interface DownloadMediaUseCase {
    operator fun invoke(
        gameId: String,
        characterId: String,
        moveList: List<Move>,
    ): Flow<Int>
}
