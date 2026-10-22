package io.github.sophon.fightingnerd.app.service

import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.RefreshEventsPort
import io.github.sophon.fightingnerd.inPort.SubscribeToRefreshEventsUseCase
import kotlinx.coroutines.flow.Flow

internal class SubscribeToRefreshEventsService(
    private val refreshEventsPort: RefreshEventsPort,
): SubscribeToRefreshEventsUseCase {
    override fun invoke(): Flow<RefreshEvent> {
        val flow = refreshEventsPort.subscribeToRefreshEvents()
        return flow
    }
}
