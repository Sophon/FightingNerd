package io.github.sophon.wiki.application.domain.service

import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.port.inbound.GetMoveListUseCase
import io.github.sophon.wiki.application.port.outbound.LoadMoveListPort
import kotlinx.coroutines.flow.Flow

internal class GetMoveListService(
    private val loadMoveListPort: LoadMoveListPort,
) : GetMoveListUseCase {
    override fun invoke(characterId: CharacterId): Flow<List<Move>> = loadMoveListPort.subscribe(characterId)
}
