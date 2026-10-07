package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.Wiki
import kotlinx.coroutines.flow.Flow

/**
 * Every wiki with at least one available game.
 */
interface SubscribeToWikisUseCase {
    operator fun invoke(): Flow<Set<Wiki>>
}
