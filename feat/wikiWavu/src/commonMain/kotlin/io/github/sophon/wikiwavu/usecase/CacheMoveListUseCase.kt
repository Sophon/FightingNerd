package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.asEmptyDataResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.wikiwavu.WavuError
import io.github.sophon.wikiwavu.data.MoveListDB
import io.github.sophon.wikiwavu.domain.model.Character
import io.github.sophon.wikiwavu.domain.model.Move

class CacheMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<WavuError> {
        return db.insertMoveList(
            charName = character.id.lowercase(),
            moveList = moveList
        )
            .asEmptyDataResult()
            .flatMap {
                character.aliasList.fold(Result.Success(Unit) as EmptyResult<WavuError>) { acc, alias ->
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
