package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.wikiwavu.util.cleanMoveInput

internal class FetchMoveUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        moveQuery: String
    ): Result<Move, WikiError> {
        return db.fetchMoveDataFor(
            charName = charName,
            moveQuery = moveQuery.cleanMoveInput(),
        )
    }
}