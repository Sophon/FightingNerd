package io.github.sophon.wikiwavu.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRemoteAdapter
import io.github.sophon.core.wiki.data.MoveRemoteAdapter
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

internal class WavuCharacterRemoteAdapter(
    private val source: WavuWikiDataSource,
) : CharacterRemoteAdapter {

    override suspend fun download(): Result<List<Character>, DataError> {
        val result = source.downloadCharacterList().map { it.toDomain() }
        return result
    }
}

internal class WavuMoveRemoteAdapter(
    private val source: WavuWikiDataSource,
    game: Game,
) : MoveRemoteAdapter {

    private val moveTable: String = WavuTables.getTable(game.id)?.moves
        ?: error("${game.id} is not supported by Wavu.")

    override suspend fun download(character: Character): Result<List<Move>, DataError> {
        val result = source.downloadMoveList(table = moveTable, character = character)
            .map { it.toDomain(character) }
        return result
    }
}
