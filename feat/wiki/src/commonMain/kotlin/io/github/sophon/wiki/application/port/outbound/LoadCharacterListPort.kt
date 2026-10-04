package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.wiki.Game
import kotlinx.coroutines.flow.Flow

internal interface LoadCharacterListPort {
    fun subscribe(game: Game): Flow<List<Character>>
}
