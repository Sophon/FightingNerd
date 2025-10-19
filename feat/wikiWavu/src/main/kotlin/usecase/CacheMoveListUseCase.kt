package usecase

import WavuError
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import dataLocal.MoveListDB
import model.Character
import model.Move

class CacheMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        character: Character,
        moveList: Map<String, Move>
    ): EmptyResult<WavuError> {
        db.insertMoveList(
            charName = character.name.lowercase(),
            moveList = moveList
        )
        character.alias.forEach { alias ->
            db.insertMoveList(charName = alias, moveList = moveList)
        }
        return Result.Success(Unit)
    }
}