package io.github.sophon.fightingnerd.inPort

import kotlinx.coroutines.flow.Flow

/**
 * Emits the IDs of the game's characters with downloaded media.
 */
interface SubscribeToOfflineMediaUseCase {
    operator fun invoke(gameId: String): Flow<Set<String>>
}
