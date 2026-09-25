package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.CharacterId
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.port.outbound.DeleteMoveListPort
import io.github.sophon.wiki.application.port.outbound.LoadLastUpdatePort
import io.github.sophon.wiki.application.port.outbound.LoadMoveListPort
import io.github.sophon.wiki.application.port.outbound.SaveMoveListPort
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

internal class SqlDelightMoveAdapter :
    LoadMoveListPort,
    SaveMoveListPort,
    LoadLastUpdatePort,
    DeleteMoveListPort {

    override fun loadMoveList(
        game: Game,
        characterId: CharacterId,
    ): Flow<List<Move>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveMoveList(
        game: Game,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override fun loadLastUpdate(game: Game): Flow<Instant?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMoveList(game: Game): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }
}
