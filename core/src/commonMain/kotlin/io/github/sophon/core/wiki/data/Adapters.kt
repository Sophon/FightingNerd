package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import kotlinx.coroutines.flow.Flow

interface CharacterRemoteAdapter {
    suspend fun download(): Result<List<Character>, DataError>
}

interface MoveRemoteAdapter {
    suspend fun download(character: Character): Result<List<Move>, DataError>
}

interface CharacterDbAdapter {
    fun insert(character: Character)
    fun selectAllFlow(): Flow<List<Character>>
    fun incrementFailureCountForAbsent(remoteIds: List<String>)
    fun deleteExceededThreshold(threshold: Long)
    fun deleteAll()
    fun transaction(block: () -> Unit)
}

interface MoveDbAdapter {
    fun insert(move: Move)
    fun selectMovesFlow(characterId: String): Flow<List<Move>>
    fun incrementFailureCountForAbsent(characterId: String, remoteIds: List<String>)
    fun deleteExceededThreshold(characterId: String, threshold: Long)
    fun deleteAll()
    fun transaction(block: () -> Unit)
}
