package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal interface LoadLastUpdatePort {
    fun subscribe(game: Game): Flow<Instant?>
}
