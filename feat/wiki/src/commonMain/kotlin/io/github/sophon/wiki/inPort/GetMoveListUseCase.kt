package io.github.sophon.wiki.inPort

import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import kotlinx.coroutines.flow.Flow

interface GetMoveListUseCase {
    operator fun invoke(characterId: CharacterId): Flow<List<Move>>
}
