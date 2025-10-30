package com.example.wikiwavu.usecase

import com.example.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move

class FetchMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WavuError> {
        return db.fetchMoveListFor(charName)
    }
}