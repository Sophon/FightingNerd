package io.github.sophon.xko.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

internal class FetchMoveUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(charName: String, moveQuery: String): Result<Move, WikiError> {
        return db.fetchMoveDataFor(charName, moveQuery)
    }
}