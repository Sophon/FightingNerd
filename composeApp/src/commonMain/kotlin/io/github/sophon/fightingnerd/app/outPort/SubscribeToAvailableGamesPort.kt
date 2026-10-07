package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

internal interface SubscribeToAvailableGamesPort {
    fun subscribeToAvailableGames(): Flow<Set<Game>>
}
