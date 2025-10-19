package dataLocal

import WavuError
import com.example.core.domain.Result
import model.Move

//TODO: use Result -> think about Errors
interface MoveListDB {
    suspend fun fetchMoveListFor(charName: String): Map<String, Move>
    suspend fun fetchMoveDataFor(charName: String, moveQuery: String): Result<Move, WavuError>
    suspend fun insertMoveList(charName: String, moveList: Map<String, Move>)
}