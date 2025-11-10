package io.github.sophon.wikiSuperCombo.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move

internal class CacheMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        character: Character,
        moveList: List<Move>
    ): EmptyResult<WikiError> {
        return db.insertMoveList(
            charName = character.id.lowercase(),
            moveList = moveList,
        )
            .asEmptyDataResult()
            .flatMap {
                character.aliasList.fold(Result.Success(Unit) as EmptyResult<WikiError>) { acc, alias ->
                    acc.flatMap {
                        db.insertMoveList(
                            charName = alias,
                            moveList = moveList,
                        ).asEmptyDataResult()
                    }
                }
            }
    }
}