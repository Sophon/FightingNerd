package usecase

import WavuError
import com.example.core.domain.Result
import data.MoveListDB
import domain.model.Move
import util.cleanMoveInput

class FetchMoveDataUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        moveQuery: String
    ): Result<Move, WavuError> {
        return db.fetchMoveDataFor(
            charName = charName,
            moveQuery = moveQuery.cleanMoveInput(),
        )
    }
}