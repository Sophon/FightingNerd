package io.github.sophon.cornerman.screens.moveList.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.aakira.napier.Napier
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.cornerman.screens.moveList.domain.MoveListError
import io.github.sophon.cornerman.screens.moveList.domain.toDomain
import io.github.sophon.cornerman.screens.moveList.domain.toEntity
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class RoomMoveListDB(
    private val dao: MoveListDao,
    private val dataStore: DataStore<Preferences>
): MoveListDB<MoveListError> {
    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<List<Move>, MoveListError> {
        return try {
            val moveEntities = dao.fetchMoveListFor(charName)
            if (moveEntities.isEmpty()) {
                Result.Error(MoveListError.UNKNOWN_CHARACTER)
            } else {
                Result.Success(moveEntities.map { it.toDomain() })
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(MoveListError.DATABASE_ERROR)
        }
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String,
    ): Result<Move, MoveListError> {
        return try {
            val moveEntity = dao.fetchMoveDataFor(charName, moveQuery)
            if (moveEntity == null) {
                Result.Error(MoveListError.UNKNOWN_MOVE)
            } else {
                Result.Success(moveEntity.toDomain())
            }
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(MoveListError.DATABASE_ERROR)
        }
    }

    override suspend fun insertMoveList(
        charName: String,
        moveList: List<Move>
    ): EmptyResult<MoveListError> {
        return try {
            dao.insertMoveList(moveList.map { it.toEntity() })
            dataStore.edit { it[KEY_LAST_INSERT] = Clock.System.now().toString() }
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(MoveListError.DATABASE_ERROR)
        }
    }

    override suspend fun wipe(): EmptyResult<MoveListError> {
        return try {
            dao.deleteAllMoves()
            dataStore.edit { it.remove(KEY_LAST_INSERT) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(MoveListError.DATABASE_ERROR)
        }
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, MoveListError> {
        return try {
            val timestampString = dataStore.data.first()[KEY_LAST_INSERT]
            val instant = timestampString?.let { Instant.parse(it) }
            Result.Success(instant)
        } catch (e: Exception) {
            Napier.e(tag = TAG) { e.toString() }
            Result.Error(MoveListError.DATABASE_ERROR)
        }
    }

    private companion object {
        private const val TAG = "RoomMoveListDB"
        private val KEY_LAST_INSERT = stringPreferencesKey("last_insert_timestamp")
    }
}