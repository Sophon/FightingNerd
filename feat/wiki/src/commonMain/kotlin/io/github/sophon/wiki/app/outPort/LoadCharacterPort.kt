package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId

internal interface LoadCharacterPort {
    suspend fun get(characterId: CharacterId): Character?
}
