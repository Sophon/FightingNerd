package usecase

import WavuError
import com.example.core.domain.EmptyResult
import com.example.core.domain.Result
import dataLocal.MoveListDB
import model.CharacterMoveList

class CacheMoveListUseCase(
    private val db: MoveListDB,
) {
    suspend fun invoke(
        characterMoveList: CharacterMoveList,
    ): EmptyResult<WavuError> {
        db.insertMoveList(
            charName = characterMoveList.character.name.lowercase(),
            moveList = characterMoveList.moveList,
        )
        characterMoveList.character.alias.forEach { alias ->
            db.insertMoveList(charName = alias, moveList = characterMoveList.moveList)
        }
        return Result.Success(Unit)
    }
}