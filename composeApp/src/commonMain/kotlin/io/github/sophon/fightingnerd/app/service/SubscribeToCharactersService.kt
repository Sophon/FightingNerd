package io.github.sophon.fightingnerd.app.service

import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.inPort.SubscribeToCharactersUseCase
import kotlinx.coroutines.flow.Flow

internal class SubscribeToCharactersService(
    private val characterPort: CharacterPort,
): SubscribeToCharactersUseCase {
    override fun invoke(game: Game): Flow<List<Character>> {
        val flow = characterPort.subscribeToCharacters(game.id)
        return flow
    }
}
