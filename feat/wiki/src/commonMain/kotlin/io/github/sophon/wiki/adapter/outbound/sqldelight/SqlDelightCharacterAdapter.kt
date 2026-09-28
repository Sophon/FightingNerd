package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.port.outbound.DeleteCharacterListPort
import io.github.sophon.wiki.application.port.outbound.LoadCharacterListPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterMoveListPort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class SqlDelightCharacterAdapter :
    LoadCharacterListPort,
    SaveCharacterMoveListPort,
    DeleteCharacterListPort {

    override fun subscribe(game: Game): Flow<List<Character>> {
        Napier.w(tag = TAG) { "subscribe(${game.id}) - not implemented" }
        return flowOf(emptyList())
    }

    override suspend fun save(
        game: Game,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local> {
        Napier.w(tag = TAG) { "save(${game.id}, ${character.id}, ${moveList.size} moves) - not implemented" }
        return Result.Success(Unit)
    }

    override suspend fun delete(game: Game): EmptyResult<DataError.Local> {
        Napier.w(tag = TAG) { "delete(${game.id}) - not implemented" }
        return Result.Success(Unit)
    }


    private companion object {
        const val TAG = "SqlDelightCharacterAdapter"
    }
}
