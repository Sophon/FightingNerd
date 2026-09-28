package io.github.sophon.wiki.application.port.inbound

import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import kotlinx.coroutines.flow.Flow

interface GetMoveListUseCase {
    operator fun invoke(characterId: CharacterId): Flow<List<Move>>
}
