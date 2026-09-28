package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.wiki.application.domain.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

/**
 * Downloads and saves the characters and moves of every enabled game. Emits [RefreshEvent.Failed] for every
 * character list or move list that failed, then a single [RefreshEvent.Finished].
 * Waits for the config if the wiki isn't configured yet.
 *
 * The flow is cold - the refresh runs while it's collected and stops when the collection is cancelled.
 * Overlapping collections run one after another - two refreshes never write at the same time.
 */
interface RefreshDataUseCase {
    operator fun invoke(): Flow<RefreshEvent>
}
