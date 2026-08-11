package io.github.sophon.wikiSuperCombo.data.remote

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
import io.github.sophon.wikiSuperCombo.data.SuperComboDataSource
import io.github.sophon.wikiSuperCombo.data.SuperComboTables
import io.github.sophon.wikiSuperCombo.data.toDomain

internal class SuperComboCharacterRemoteAdapter(
    private val source: SuperComboDataSource,
    private val game: Game,
) : CharacterRemoteAdapter {

    private val tables: QueryTable = SuperComboTables.getTable(game.id)
        ?: error("${game.id} is not supported by SuperCombo.")

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

internal class SuperComboMoveRemoteAdapter(
    private val source: SuperComboDataSource,
    private val game: Game,
) : MoveRemoteAdapter {

    private val tables: QueryTable = SuperComboTables.getTable(game.id)
        ?: error("${game.id} is not supported by SuperCombo.")

    override suspend fun download(character: Character): Result<List<Move>, DataError> {
        val result = source.downloadMoveList(table = tables.moves, character = character)
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
