package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

interface SubscribeToCharactersUseCase {
    operator fun invoke(game: Game): Flow<List<Character>>
}
