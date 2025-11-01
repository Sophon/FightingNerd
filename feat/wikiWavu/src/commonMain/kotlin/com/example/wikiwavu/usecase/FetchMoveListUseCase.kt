package com.example.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Move
import io.github.aakira.napier.Napier

class FetchMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WavuError> {
        return when (val result = db.fetchMoveListFor(charName)) {
            is Result.Success -> {
                if (result.data.isEmpty()) {
                    Napier.d(tag = "Sorry") { "database is missing the char, need to download" }
                }

                Result.Success(result.data)
            }
            is Result.Error -> Result.Error(result.error)
        }
    }
}