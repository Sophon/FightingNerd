package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

internal class FetchMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(charName: String): Result<List<Move>, WikiError> {
        return db.fetchMoveListFor(charName)
    }
}