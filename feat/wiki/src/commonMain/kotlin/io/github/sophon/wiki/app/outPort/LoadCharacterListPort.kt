package io.github.sophon.wiki.app.outPort

import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow

internal interface LoadCharacterListPort {
    fun subscribe(game: Game): Flow<List<Character>>
}
