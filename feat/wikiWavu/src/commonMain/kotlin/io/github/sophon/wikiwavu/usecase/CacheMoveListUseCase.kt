package io.github.sophon.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
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
        db.insertMoveList(
            charName = character.name.lowercase(),
            moveList = moveList,
        )
        character.alias.forEach { alias ->
            db.insertMoveList(charName = alias, moveList = moveList)
        }
        return Result.Success(Unit)
    }
}