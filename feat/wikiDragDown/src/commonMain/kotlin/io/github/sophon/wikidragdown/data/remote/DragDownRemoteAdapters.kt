package io.github.sophon.wikidragdown.data.remote

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRemoteAdapter
import io.github.sophon.core.wiki.data.MoveRemoteAdapter
import io.github.sophon.core.wiki.data.QueryTable
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

internal class DragDownCharacterRemoteAdapter(
    private val source: DragDownDataSource,
    private val game: Game,
) : CharacterRemoteAdapter {

    private val tables: QueryTable = DragDownTables.getTable(game.id)
        ?: error("${game.id} is not supported by DragDown.")

    override suspend fun download(): Result<List<Character>, DataError> {
        val result = source.downloadCharacterList(table = tables.character)
            .flatMap { dto ->
                source.resolveCharacterImageUrls(dto).map { imageUrlMap ->
                    val characterList = dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
                    characterList
                }
            }
        return result
    }
}

internal class DragDownMoveRemoteAdapter(
    private val source: DragDownDataSource,
    private val game: Game,
) : MoveRemoteAdapter {

    private val tables: QueryTable = DragDownTables.getTable(game.id)
        ?: error("${game.id} is not supported by DragDown.")

    override suspend fun download(character: Character): Result<List<Move>, DataError> {
        val result = source.downloadMoveList(tables.moves, character)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto).map { imageUrlMap ->
                    val moveList = dto.toDomain(
                        character = character,
                        imageUrlMap = imageUrlMap,
                    )
                    moveList
                }
            }
        return result
    }
}
