package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.model.Move
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveListDB

class FetchMovesWithPropertyUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        predicate: (Move) -> Boolean
    ): Result<List<Move>, WavuError> {
        return db.fetchMoveListFor(charName.lowercase())
            .map { moveList ->
                moveList.filter(predicate)
            }
    }
}