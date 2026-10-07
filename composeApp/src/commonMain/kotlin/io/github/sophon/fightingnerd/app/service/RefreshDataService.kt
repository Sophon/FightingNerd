package io.github.sophon.fightingnerd.app.service

import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.inPort.RefreshDataUseCase
import kotlinx.coroutines.flow.Flow

internal class RefreshDataService(
    private val refreshWikiPort: RefreshWikiPort,
): RefreshDataUseCase {
    override fun invoke(): Flow<RefreshEvent> {
        val flow = refreshWikiPort.refresh()
        return flow
    }
}
