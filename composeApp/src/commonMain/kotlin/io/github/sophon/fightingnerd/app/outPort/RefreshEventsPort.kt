package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

internal interface RefreshEventsPort {
    fun subscribeToRefreshEvents(): Flow<RefreshEvent>
}
