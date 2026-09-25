package io.github.sophon.wiki.application.domain.service

import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.port.inbound.GetMoveListUseCase
import kotlinx.coroutines.flow.Flow

internal class GetMoveListService : GetMoveListUseCase {
    override fun invoke(characterId: CharacterId): Flow<List<Move>> {
        TODO("Not yet implemented")
    }
}
