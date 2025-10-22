package dataLocal

import WavuError
import com.example.core.domain.Result
import model.Move

class InMemoryMoveListDB: MoveListDB {
    private var database: MutableMap<String, Map<String, Move>> = mutableMapOf()

    override suspend fun fetchMoveListFor(
        charName: String
    ): Result<Map<String, Move>, WavuError> {
        return database[charName]
            ?.let { Result.Success(it) }
            ?: Result.Error(WavuError.UNKNOWN_CHARACTER)
    }

    override suspend fun fetchMoveDataFor(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        val moveList = database[charName]
            ?: return Result.Error(WavuError.UNKNOWN_CHARACTER)
        val moveData = moveList[moveQuery]
            ?: return Result.Error(WavuError.UNKNOWN_MOVE)

        return Result.Success(moveData)
    }

    override suspend fun insertMoveList(charName: String, moveList: List<Move>) {
        database.put(key = charName, value = moveList.associateBy { it.id })
    }
}