package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.RefreshEvent
import kotlinx.coroutines.flow.Flow

internal interface RefreshWikiPort {
    fun refresh(): Flow<RefreshEvent>
    fun refresh(gameIdSet: Set<String>): Flow<RefreshEvent>
}
