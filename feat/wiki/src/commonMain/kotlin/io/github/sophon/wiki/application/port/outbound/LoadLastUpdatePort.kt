package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.wiki.application.domain.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal interface LoadLastUpdatePort {
    fun subscribe(game: Game): Flow<Instant?>
}
