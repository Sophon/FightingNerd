package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import kotlinx.coroutines.flow.Flow

internal interface LoadMoveListPort {
    fun subscribe(characterId: CharacterId): Flow<List<Move>>
}
