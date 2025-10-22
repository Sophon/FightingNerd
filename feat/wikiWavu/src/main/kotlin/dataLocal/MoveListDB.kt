package dataLocal

import WavuError
import com.example.core.domain.Result
import model.Move

interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Result<Map<String, Move>, WavuError>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun insertMoveList(charName: String, moveList: List<Move>)
}