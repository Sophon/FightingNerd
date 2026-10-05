package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.coroutines.asFlow
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.adapter.outbound.sqldelight.mapper.toDomain
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game
import io.github.sophon.wiki.app.outPort.DeleteMoveListPort
import io.github.sophon.wiki.app.outPort.LoadLastUpdatePort
import io.github.sophon.wiki.app.outPort.LoadMoveListPort
import io.github.sophon.wiki.app.outPort.LoadMovePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.getValue
import kotlin.time.Instant

internal class SqlDelightMoveAdapter(
    wikiDatabase: LazyWikiDB,
    private val gamePropertiesRouter: SqlDelightGamePropertiesRouter,
) : LoadMoveListPort,
    LoadMovePort,
    LoadLastUpdatePort,
    DeleteMoveListPort {
    private val database by wikiDatabase

    override fun subscribe(characterId: CharacterId): Flow<List<Move>> {
        // the query is built inside the flow - the first use opens the database, which belongs on IO
        val flow = flow {
            emitAll(database.moveQueries.selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId).asFlow())
        }
            .map { loadMoveList(characterId) }
            .flowOn(Dispatchers.IO)
        return flow
    }

    override suspend fun get(characterId: CharacterId, input: String): Move? {
        val move = withContext(Dispatchers.IO) { loadMove(characterId, input) }
        return move
    }

    // here, not in SqlDelightCharacterAdapter - LoadCharacterListPort.subscribe(Game) differs only in the return type
    override fun subscribe(game: Game): Flow<Instant?> {
        val flow = flow { emitAll(database.characterQueries.selectLastUpdate(game = game.id).asFlow()) }
            .map { query ->
                val lastUpdate = query.executeAsOneOrNull()?.let { updatedAt -> Instant.fromEpochMilliseconds(updatedAt) }
                lastUpdate
            }
            .flowOn(Dispatchers.IO)
        return flow
    }

    override suspend fun delete(game: Game): EmptyResult<DataError.Local> {
        val result = runDatabaseWrite(TAG, "delete(${game.id})") {
            database.moveQueries.deleteByGame(game = game.id)
        }
        return result
    }

    private fun loadMoveList(characterId: CharacterId): List<Move> {
        val moveList = database.transactionWithResult {
            val aliasListByRowId = database.moveAliasQueries
                .selectAliasByCharacter(game = characterId.game.id, natural_id = characterId.naturalId) { moveRowId, alias ->
                    moveRowId to alias
                }
                .executeAsList()
                .groupBy(
                    keySelector = { (moveRowId, _) -> moveRowId },
                    valueTransform = { (_, alias) -> alias },
                )
            val propertiesByRowId = gamePropertiesRouter.of(characterId.game).loadMoveProperties(characterId)

            val loadedMoveList = database.moveQueries
                .selectByCharacter(game = characterId.game.id, natural_id = characterId.naturalId)
                .executeAsList()
                .map { entity ->
                    entity.toDomain(
                        aliases = aliasListByRowId[entity.id].orEmpty(),
                        gameProperties = propertiesByRowId[entity.id],
                    )
                }
            loadedMoveList
        }
        return moveList
    }

    private fun loadMove(characterId: CharacterId, input: String): Move? {
        val move = database.transactionWithResult {
            val aliasListByRowId = database.moveAliasQueries
                .selectAliasByCharacter(game = characterId.game.id, natural_id = characterId.naturalId) { moveRowId, alias ->
                    moveRowId to alias
                }
                .executeAsList()
                .groupBy(
                    keySelector = { (moveRowId, _) -> moveRowId },
                    valueTransform = { (_, alias) -> alias },
                )
            val propertiesByRowId = gamePropertiesRouter.of(characterId.game).loadMoveProperties(characterId)

            val loadedMove = database.moveQueries
                .selectByInput(game = characterId.game.id, natural_id = characterId.naturalId, input = input)
                .executeAsOneOrNull()
                ?.let { entity ->
                    entity.toDomain(
                        aliases = aliasListByRowId[entity.id].orEmpty(),
                        gameProperties = propertiesByRowId[entity.id],
                    )
                }
            loadedMove
        }
        return move
    }


    private companion object {
        const val TAG = "SqlDelightMoveAdapter"
    }
}
