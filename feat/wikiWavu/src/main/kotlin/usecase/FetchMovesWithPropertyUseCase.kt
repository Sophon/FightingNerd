package usecase

import WavuError
import com.example.core.domain.Result
import com.example.core.domain.map
import data.MoveListDB
import domain.model.Move

class FetchMovesWithPropertyUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        predicate: (Move) -> Boolean
    ): Result<List<Move>, WavuError> {
        return db.fetchMoveListFor(charName)
            .map { map ->
                map
                    .values
                    .filter(predicate)
            }
    }
}