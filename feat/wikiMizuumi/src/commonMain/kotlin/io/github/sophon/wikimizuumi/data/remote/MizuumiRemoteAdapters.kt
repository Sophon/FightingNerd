package io.github.sophon.wikimizuumi.data.remote

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

internal class MizuumiCharacterRemoteAdapter(
    private val source: MizuumiWikiDataSource,
    private val game: Game,
    private val cache: MizuumiDataCache,
) : CharacterRemoteAdapter {

    private val gameTables: QueryTable = MizuumiTables.getTable(game.id)
        ?: error("${game.id} is not supported by Mizuumi.")

    override suspend fun download(): Result<List<Character>, DataError> {
        val result = if (game.separateCharMoveDownload) {
            source.downloadCharacterList(gameTables.character)
                .flatMap { dto ->
                    source.resolveCharacterImageUrls(dto).map { imageUrlMap ->
                        val characterList = dto.toDomain(gameId = game.id, imageUrlMap = imageUrlMap)
                        characterList
                    }
                }
        } else {
            cache.getOrFetch().map { map ->
                val characterList = map.keys.toList()
                characterList
            }
        }
        return result
    }
}

internal class MizuumiMoveRemoteAdapter(
    private val source: MizuumiWikiDataSource,
    private val game: Game,
    private val cache: MizuumiDataCache,
) : MoveRemoteAdapter {

    private val gameTables: QueryTable = MizuumiTables.getTable(game.id)
        ?: error("${game.id} is not supported by Mizuumi.")

    override suspend fun download(character: Character): Result<List<Move>, DataError> {
        val result = if (game.separateCharMoveDownload) {
            cache.getOrFetch().map { map ->
                val moveList = map
                    .filterKeys { it.remoteQueryId == character.remoteQueryId }
                    .values
                    .flatten()
                moveList
            }
        } else {
            source.downloadMoveList(table = gameTables.moves, character = character)
                .flatMap { dto ->
                    source.resolveHitboxUrls(dto).map { hitboxUrlMap ->
                        val moveList = dto.cargoquery.map {
                            it.title.toDomain(character = character, hitboxUrlMap = hitboxUrlMap)
                        }
                        moveList
                    }
                }
        }
        return result
    }
}
