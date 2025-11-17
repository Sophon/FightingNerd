package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

internal class FetchMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WikiError> {
        return when (val result = db.fetchMoveListFor(charName)) {
            is Result.Success -> {
                if (result.data.isEmpty()) {
                    Result.Error(WikiError.DatabaseError("EMPTY DATABASE"))
                } else {
                    Result.Success(result.data)
                }
            }
            is Result.Error -> Result.Error(result.error)
        }
    }
}