package io.github.sophon.wiki.adapter.outbound.sqldelight

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.wiki.application.port.outbound.DeleteCharacterListPort
import io.github.sophon.wiki.application.port.outbound.LoadCharacterListPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterListPort
import kotlinx.coroutines.flow.Flow

internal class SqlDelightCharacterAdapter :
    LoadCharacterListPort,
    SaveCharacterListPort,
    DeleteCharacterListPort {

    override fun loadCharacterList(game: Game): Flow<List<Character>> {
        TODO("Not yet implemented")
    }

    override suspend fun saveCharacterList(
        game: Game,
        characterList: List<Character>,
    ): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCharacterList(game: Game): EmptyResult<DataError.Local> {
        TODO("Not yet implemented")
    }
}
