package io.github.sophon.wiki.adapter.outbound.sqldelight

import app.cash.sqldelight.coroutines.asFlow
import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.adapter.outbound.sqldelight.mapper.toDomain
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.port.outbound.DeleteMoveListPort
import io.github.sophon.wiki.application.port.outbound.LoadLastUpdatePort
import io.github.sophon.wiki.application.port.outbound.LoadMoveListPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.getValue
import kotlin.time.Instant

internal class SqlDelightMoveAdapter(
    wikiDatabase: LazyWikiDB,
    private val gamePropertiesRouter: SqlDelightGamePropertiesRouter,
) : LoadMoveListPort,
    LoadLastUpdatePort,
    DeleteMoveListPort {
    private val database by wikiDatabase

    override fun subscribe(
        game: Game,
        characterId: CharacterId,
    ): Flow<List<Move>> {
        // the query is built inside the flow - the first use opens the database, which belongs on IO
        val flow = flow {
            emitAll(database.moveQueries.selectByCharacter(game = game.id, natural_id = characterId.value).asFlow())
        }
            .map { loadMoveList(game, characterId) }
            .flowOn(Dispatchers.IO)
        return flow
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

    private fun loadMoveList(
        game: Game,
        characterId: CharacterId,
    ): List<Move> {
        val moveList = database.transactionWithResult {
            val aliasListByRowId = database.moveAliasQueries
                .selectAliasByCharacter(game = game.id, natural_id = characterId.value) { moveRowId, alias ->
                    moveRowId to alias
                }
                .executeAsList()
                .groupBy(
                    keySelector = { (moveRowId, _) -> moveRowId },
                    valueTransform = { (_, alias) -> alias },
                )
            val propertiesByRowId = gamePropertiesRouter.of(game).loadMoveProperties(game, characterId)

            val loadedMoveList = database.moveQueries
                .selectByCharacter(game = game.id, natural_id = characterId.value)
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


    private companion object {
        const val TAG = "SqlDelightMoveAdapter"
    }
}
