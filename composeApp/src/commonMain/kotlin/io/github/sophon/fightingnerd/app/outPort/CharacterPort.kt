package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

internal interface CharacterPort {
    fun subscribeToCharacters(game: Game): Flow<List<Character>>
}
