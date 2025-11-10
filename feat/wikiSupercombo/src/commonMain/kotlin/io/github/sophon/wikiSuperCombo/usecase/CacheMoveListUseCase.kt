package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move

internal class CacheMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        charName: String,
        moveList: List<Move>
    ): EmptyResult<WikiError> {
        return db.insertMoveList(charName, moveList)
    }
}