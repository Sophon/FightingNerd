package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.featureConfig.model.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal interface LoadLastUpdatePort {
    fun loadLastUpdate(game: Game): Flow<Instant?>
}
