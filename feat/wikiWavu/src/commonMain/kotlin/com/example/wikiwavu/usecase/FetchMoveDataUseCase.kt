package com.example.wikiwavu.usecase

import com.example.wikiwavu.WavuError
import com.example.core.domain.Result
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move
import com.example.wikiwavu.util.cleanMoveInput

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