package io.github.sophon.fightingnerd.inPort

import io.github.sophon.fightingnerd.app.model.Game
import kotlinx.coroutines.flow.Flow

interface CheckCharacterHasMovesUseCase {
    operator fun invoke(game: Game, characterId: String): Flow<Boolean>
}
