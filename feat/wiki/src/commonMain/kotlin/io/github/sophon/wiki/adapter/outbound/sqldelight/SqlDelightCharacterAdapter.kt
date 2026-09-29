package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.coroutines.asFlow
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.mapper.toDomain
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.port.outbound.DeleteCharacterListPort
import io.github.sophon.wiki.application.port.outbound.LoadCharacterListPort
import io.github.sophon.wiki.application.port.outbound.SaveCharacterMoveListPort
import io.github.sophon.wiki.application.port.outbound.StrikeCharacterListPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.getValue
import kotlin.time.Clock

internal class SqlDelightCharacterAdapter(
    wikiDatabase: LazyWikiDB,
    private val gamePropertiesRouter: SqlDelightGamePropertiesRouter,
    private val clock: Clock,
) : LoadCharacterListPort,
    SaveCharacterMoveListPort,
    StrikeCharacterListPort,
    DeleteCharacterListPort {
    private val database by wikiDatabase

    override fun subscribe(game: Game): Flow<List<Character>> {
        // the query is built inside the flow - the first use opens the database, which belongs on IO
        val flow = flow { emitAll(database.characterQueries.selectByGame(game = game.id).asFlow()) }
            .map { loadCharacterList(game) }
            .flowOn(Dispatchers.IO)
        return flow
    }

    override suspend fun save(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local> {
        val game = character.id.game
        val result = runDatabaseWrite(TAG, "save(${game.id}, ${character.id.naturalId})") {
            database.transaction {
                val characterRowId = upsertCharacter(game, character)
                replaceCharacterAliasList(game, characterRowId, character.aliasList)
                val moveRowIdList = upsertMoveList(game, characterRowId, moveList)
                replaceMoveAliasList(characterRowId, moveList, moveRowIdList)
            }
        }
        return result
    }

    override suspend fun strike(
        game: Game,
        downloadedIdSet: Set<CharacterId>,
    ): EmptyResult<DataError.Local> {
        val result = runDatabaseWrite(TAG, "strike(${game.id})") {
            database.transaction {
                database.characterQueries.strikeAbsent(
                    game = game.id,
                    natural_id = downloadedIdSet.map { id -> id.naturalId },
                )
                database.characterQueries.deleteStruck(game = game.id, strike_count = STRIKE_LIMIT)
            }
        }
        return result
    }

    override suspend fun delete(game: Game): EmptyResult<DataError.Local> {
        val result = runDatabaseWrite(TAG, "delete(${game.id})") {
            database.characterQueries.deleteByGame(game = game.id)
        }
        return result
    }

    private fun loadCharacterList(game: Game): List<Character> {
        val characterList = database.transactionWithResult {
            val aliasListByRowId = database.characterAliasQueries
                .selectAliasByGame(game = game.id) { characterRowId, alias -> characterRowId to alias }
                .executeAsList()
                .groupBy(
                    keySelector = { (characterRowId, _) -> characterRowId },
                    valueTransform = { (_, alias) -> alias },
                )
            val propertiesByRowId = gamePropertiesRouter.of(game).loadCharacterProperties(game)

            val loadedCharacterList = database.characterQueries
                .selectByGame(game = game.id)
                .executeAsList()
                .map { entity ->
                    entity.toDomain(
                        game = game,
                        aliasList = aliasListByRowId[entity.id].orEmpty(),
                        gameProperties = propertiesByRowId[entity.id],
                    )
                }
            loadedCharacterList
        }
        return characterList
    }

    private fun upsertCharacter(
        game: Game,
        character: Character,
    ): Long {
        database.characterQueries.upsert(
            game = game.id,
            natural_id = character.id.naturalId,
            remote_query_id = character.remoteQueryId,
            display_name = character.displayName,
            wiki_url = character.wikiUrl,
            icon_id = character.images?.iconId,
            icon_url = character.images?.iconUrl,
            banner_url = character.images?.bannerUrl,
            hp = character.hp,
            umo = character.umo,
            updated_at = clock.now().toEpochMilliseconds(),
        )
        // re-selected by natural key - no RETURNING before SQLite 3.35 (minSdk 30 ships 3.28),
        // and last_insert_rowid() isn't set by the update path
        val characterRowId = database.characterQueries
            .selectId(game = game.id, natural_id = character.id.naturalId)
            .executeAsOne()

        character.gameProperties?.let { properties ->
            gamePropertiesRouter.of(game).saveCharacterProperties(characterRowId, properties)
        }
        return characterRowId
    }

    private fun replaceCharacterAliasList(
        game: Game,
        characterRowId: Long,
        aliasList: List<String>,
    ) {
        database.characterAliasQueries.deleteByCharacter(character_id = characterRowId)
        aliasList.forEach { alias ->
            database.characterAliasQueries.insert(game = game.id, alias = alias, character_id = characterRowId)
        }
    }

    /**
     * Mark → upsert → sweep - every move of the character gets a strike, the upsert clears it for the downloaded ones,
     * and a move missing from [STRIKE_LIMIT] saves in a row is deleted.
     */
    private fun upsertMoveList(
        game: Game,
        characterRowId: Long,
        moveList: List<Move>,
    ): List<Long> {
        val gameProperties = gamePropertiesRouter.of(game)

        database.moveQueries.strikeByCharacter(character_id = characterRowId)
        val moveRowIdList = moveList.mapIndexed { position, move ->
            val moveRowId = upsertMove(characterRowId, position, move)
            move.gameProperties?.let { properties -> gameProperties.saveMoveProperties(moveRowId, properties) }
            moveRowId
        }
        database.moveQueries.deleteStruckByCharacter(character_id = characterRowId, strike_count = STRIKE_LIMIT)

        return moveRowIdList
    }

    private fun upsertMove(
        characterRowId: Long,
        position: Int,
        move: Move,
    ): Long {
        database.moveQueries.upsert(
            character_id = characterRowId,
            input = move.input,
            remote_id = move.remoteId,
            position = position.toLong(),
            name = move.name,
            damage = move.damage,
            startup = move.startup,
            on_block = move.onBlock,
            on_hit = move.onHit,
            on_ch = move.onCH,
            active = move.active,
            cancel = move.cancel,
            recovery = move.recovery,
            guard = move.guard,
            invulnerability = move.invulnerability,
            type = move.type,
            is_throw = move.isThrow,
            notes = move.notes,
            wiki_url = move.urls.wikiUrl,
            video_id = move.urls.videoId,
            video_url = move.urls.videoUrl,
            hitbox_image_list = move.urls.hitboxImageList,
            move_image_list = move.urls.moveImageList,
        )
        val moveRowId = database.moveQueries
            .selectId(character_id = characterRowId, input = move.input)
            .executeAsOne()
        return moveRowId
    }

    /**
     * Rebuilt on every save - first the inputs of all the character's moves, so an input beats an alias,
     * then the aliases in wiki order, so the first alias written wins.
     */
    private fun replaceMoveAliasList(
        characterRowId: Long,
        moveList: List<Move>,
        moveRowIdList: List<Long>,
    ) {
        database.moveAliasQueries.deleteByCharacter(character_id = characterRowId)
        database.moveAliasQueries.insertInputsByCharacter(character_id = characterRowId)
        moveList.zip(moveRowIdList).forEach { (move, moveRowId) ->
            move.aliases.forEach { alias ->
                database.moveAliasQueries.insertAlias(character_id = characterRowId, alias = alias, move_id = moveRowId)
            }
        }
    }


    private companion object {
        const val TAG = "SqlDelightCharacterAdapter"
        const val STRIKE_LIMIT = 5L
    }
}
