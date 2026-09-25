package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import kotlinx.coroutines.flow.Flow

interface GetMoveListUseCase {
    operator fun invoke(characterId: CharacterId): Flow<List<Move>>
}
