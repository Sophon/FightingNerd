package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.model.Move
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveListDB
import io.github.sophon.wikiwavu.util.cleanMoveInput

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