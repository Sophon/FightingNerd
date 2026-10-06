package io.github.sophon.wiki.app.service

import io.github.sophon.wiki.app.outPort.LoadMoveListPort
import io.github.sophon.wiki.inPort.GetMoveListUseCase
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import kotlinx.coroutines.flow.Flow

internal class GetMoveListService(
    private val loadMoveListPort: LoadMoveListPort,
) : GetMoveListUseCase {
    override fun invoke(characterId: CharacterId): Flow<List<Move>> = loadMoveListPort.subscribe(characterId)
}
