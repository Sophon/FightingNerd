package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.Release
import kotlinx.coroutines.flow.Flow

/**
 * Emits the newest app release the user hasn't seen yet; on first launch, the release of the current version.
 */
interface SubscribeToUnseenReleaseUseCase {
    operator fun invoke(): Flow<Release>
}
