package io.github.sophon.wikidustloop.data.remote

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

internal class DustLoopCharacterRemoteAdapter(
    private val source: DustLoopDataSource,
    private val game: Game,
) : CharacterRemoteAdapter {

    private val tables: QueryTable = DustLoopTables.getTable(game.id)
        ?: error("${game.id} is not supported by DustLoop.")

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

internal class DustLoopMoveRemoteAdapter(
    private val source: DustLoopDataSource,
    private val game: Game,
) : MoveRemoteAdapter {

    private val tables: QueryTable = DustLoopTables.getTable(game.id)
        ?: error("${game.id} is not supported by DustLoop.")

    override suspend fun download(character: Character): Result<List<Move>, DataError> {
        val result = source.downloadMoveList(tables.moves, character)
            .flatMap { dto ->
                source.resolveHitboxUrls(dto).map { imageUrlMap ->
                    val moveList = dto.toDomain(
                        gameId = game.id,
                        character = character,
                        imageUrlMap = imageUrlMap,
                    )
                    moveList
                }
            }
        return result
    }
}
