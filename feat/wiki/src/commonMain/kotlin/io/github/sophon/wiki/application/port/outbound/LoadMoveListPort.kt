package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import kotlinx.coroutines.flow.Flow

internal interface LoadMoveListPort {
    fun subscribe(characterId: CharacterId): Flow<List<Move>>
}
