package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

/**
 * The events of every refresh, whoever started it. Hot, no replay.
 * Every refreshed game ends with a [RefreshEvent.Finished], cancelled and crashed games too.
 * A late subscriber can see a [RefreshEvent.Progress] without the [RefreshEvent.Started] before it.
 */
interface SubscribeToRefreshEventsUseCase {
    operator fun invoke(): Flow<RefreshEvent>
}
