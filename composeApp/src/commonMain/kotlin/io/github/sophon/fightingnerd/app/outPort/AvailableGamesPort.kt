package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

internal interface AvailableGamesPort {
    fun subscribe(): Flow<Set<Game>>
}
