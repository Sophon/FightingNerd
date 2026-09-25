package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import kotlinx.coroutines.flow.Flow

internal interface LoadCharacterListPort {
    fun loadCharacterList(game: Game): Flow<List<Character>>
}
