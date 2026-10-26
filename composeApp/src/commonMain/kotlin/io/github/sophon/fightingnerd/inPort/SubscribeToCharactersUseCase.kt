package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.game.Character
import io.github.sophon.fightingnerd.app.model.game.Game
import kotlinx.coroutines.flow.Flow

interface SubscribeToCharactersUseCase {
    operator fun invoke(game: Game): Flow<List<Character>>
}
