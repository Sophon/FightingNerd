package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.game.Character
import kotlinx.coroutines.flow.Flow

internal interface CharacterPort {
    fun subscribeToCharacters(gameId: String): Flow<List<Character>>
}
