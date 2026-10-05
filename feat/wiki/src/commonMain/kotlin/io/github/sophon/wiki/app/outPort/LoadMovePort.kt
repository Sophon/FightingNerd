package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move

internal interface LoadMovePort {
    suspend fun get(characterId: CharacterId, input: String): Move?
}
