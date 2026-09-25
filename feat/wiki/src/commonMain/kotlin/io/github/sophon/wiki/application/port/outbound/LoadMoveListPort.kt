package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import kotlinx.coroutines.flow.Flow

internal interface LoadMoveListPort {
    fun loadMoveList(
        game: Game,
        characterId: CharacterId,
    ): Flow<List<Move>>
}
