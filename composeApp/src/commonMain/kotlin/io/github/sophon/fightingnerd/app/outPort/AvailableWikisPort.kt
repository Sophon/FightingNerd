package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.Wiki
import kotlinx.coroutines.flow.Flow

internal interface AvailableWikisPort {
    fun subscribeToWikis(): Flow<Set<Wiki>>
}
