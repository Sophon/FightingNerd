package com.example.wikiwavu.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import com.example.wikiwavu.WavuError
import com.example.wikiwavu.data.MoveListDB
import com.example.wikiwavu.domain.model.Character
import com.example.wikiwavu.domain.model.Move

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