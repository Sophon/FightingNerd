package io.github.sophon.cornerman.screens.moveList.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.cornerman.screens.moveList.domain.MoveCategory
import io.github.sophon.cornerman.screens.moveList.domain.MoveListError
import io.github.sophon.cornerman.screens.moveList.domain.toDomain
import io.github.sophon.cornerman.screens.moveList.domain.toDomainError

internal class FetchMoveListUseCase(
    private val wikiCall: suspend (String) -> Result<List<Move>, WikiError>
) {
    suspend fun invoke(charName: String): Result<List<MoveCategory>, MoveListError> {
        return when (val result = wikiCall(charName)) {
            is Result.Success -> {
                Result.Success(result.data.toDomain())
            }
            is Result.Error -> {
                Result.Error(result.error.toDomainError())
            }
        }
    }
}