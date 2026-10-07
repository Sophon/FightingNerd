package io.github.sophon.fightingnerd.app.outPort

import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal interface LastUpdatePort {
    fun subscribeToLastUpdate(gameId: String): Flow<Instant?>
}
