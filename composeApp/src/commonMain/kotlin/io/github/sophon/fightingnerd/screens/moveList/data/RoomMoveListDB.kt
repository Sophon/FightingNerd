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
import io.github.sophon.core.wiki.domain.model.Character
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
                Result.Error(WikiError.UnknownCharacter(charName))
            } else {
                Result.Success(moveEntities.map { it.toDomain() })
            }
        } catch (e: Exception) {
            val error = e.toString()
            Napier.e(tag = TAG) { error }
            Result.Error(WikiError.DatabaseError(error))
        }
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String,
    ): Result<Move, WikiError> {
        return try {
            val moveEntity = dao.fetchMoveDataFor(charName, moveQuery)
            if (moveEntity == null) {
                Result.Error(WikiError.UnknownMove(charName, moveQuery))
            } else {
                Result.Success(moveEntity.toDomain())
            }
        } catch (e: Exception) {
            val error = e.toString()
            Napier.e(tag = TAG) { error }
            Result.Error(WikiError.DatabaseError(error))
        }
    }

    override suspend fun insertMoveList(
        character: Character,
        moveList: List<Move>
    ): EmptyResult<WikiError> {
        return try {
            dao.insertMoveList(moveList.map { it.toEntity() })
            dataStore.edit { it[KEY_LAST_INSERT] = Clock.System.now().toString() }
            Result.Success(Unit)
        } catch (e: Exception) {
            val error = e.toString()
            Napier.e(tag = TAG) { error }
            Result.Error(WikiError.DatabaseError(error))
        }
    }

    override suspend fun wipe(): EmptyResult<WikiError> {
        return try {
            dao.deleteAllMoves()
            dataStore.edit { it.remove(KEY_LAST_INSERT) }
            Result.Success(Unit)
        } catch (e: Exception) {
            val error = e.toString()
            Napier.e(tag = TAG) { error }
            Result.Error(WikiError.DatabaseError(error))
        }
    }

    override suspend fun getLastInsertTimeStamp(): Result<Instant?, WikiError> {
        return try {
            val timestampString = dataStore.data.first()[KEY_LAST_INSERT]
            val instant = timestampString?.let { Instant.parse(it) }
            Result.Success(instant)
        } catch (e: Exception) {
            val error = e.toString()
            Napier.e(tag = TAG) { error }
            Result.Error(WikiError.DatabaseError(error))
        }
    }

    private companion object {
        private const val TAG = "RoomMoveListDB"
        private val KEY_LAST_INSERT = stringPreferencesKey("last_insert_timestamp")
    }
}