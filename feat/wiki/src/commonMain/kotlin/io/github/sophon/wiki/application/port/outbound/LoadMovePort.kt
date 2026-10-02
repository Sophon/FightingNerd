package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move

internal interface LoadMovePort {
    suspend fun get(characterId: CharacterId, input: String): Move?
}
