package io.github.sophon.fightingnerd.screens.moveList.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class RoomMoveListDB(
    private val dao: MoveListDao,
    private val dataStore: DataStore<Preferences>
): MoveListDB {
    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, WikiError> {
        return try {
            val moveEntities = dao.fetchMoveListFor(charName)
            if (moveEntities.isEmpty()) {
                Result.Error(WikiError.UNKNOWN_CHARACTER)
            } else {
                Result.Success(moveEntities.map { it.toDomain() })
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return try {
            val moveEntity = dao.fetchMoveDataFor(charName, moveQuery)
            if (moveEntity == null) {
                Result.Error(WikiError.UNKNOWN_MOVE)
            } else {
                Result.Success(moveEntity.toDomain())
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun insertMoveList(
        charName: String,
        moveList: List<Move>
    ): EmptyResult<WikiError> {
        return try {
            dao.insertMoveList(moveList.map { it.toEntity() })
            dataStore.edit { it[KEY_LAST_INSERT] = Clock.System.now().toString() }
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        return try {
            dao.deleteAllMoves()
            dataStore.edit { it.remove(KEY_LAST_INSERT) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        return try {
            val timestampString = dataStore.data.first()[KEY_LAST_INSERT]
            val instant = timestampString?.let { Instant.parse(it) }
            Result.Success(instant)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(WikiError.DATABASE_ERROR)
        }
    }

    private companion object {
        private const val TAG = "RoomMoveListDB"
        private val KEY_LAST_INSERT = stringPreferencesKey("last_insert_timestamp")
    }
}