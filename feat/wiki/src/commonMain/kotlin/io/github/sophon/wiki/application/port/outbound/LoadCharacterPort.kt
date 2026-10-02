package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId

internal interface LoadCharacterPort {
    suspend fun get(characterId: CharacterId): Character?
}
