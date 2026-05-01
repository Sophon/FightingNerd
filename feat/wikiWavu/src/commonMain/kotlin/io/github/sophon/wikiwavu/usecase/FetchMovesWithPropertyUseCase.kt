package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

internal class FetchMovesWithPropertyUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        predicate: (Move) -> Boolean
    ): Result<List<Move>, WikiError> {
        return db.fetchMoveListFor(charName.lowercase())
            .map { moveList ->
                moveList.filter(predicate)
            }
    }
}