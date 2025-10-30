package com.example.wikiwavu.usecase

import com.example.wikiwavu.WavuError
import com.example.core.domain.Result
import com.example.core.domain.map
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move

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