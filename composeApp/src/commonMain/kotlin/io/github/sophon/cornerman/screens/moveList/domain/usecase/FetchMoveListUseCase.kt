package io.github.sophon.cornerman.screens.moveList.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.cornerman.screens.moveList.domain.MoveCategory
import io.github.sophon.cornerman.screens.moveList.domain.MoveListError
import io.github.sophon.cornerman.screens.moveList.domain.toDomain
import io.github.sophon.cornerman.screens.moveList.domain.toDomainError
import io.github.sophon.wikiwavu.WavuWikiClient

class FetchMoveListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<MoveCategory>, MoveListError> {
        return when (val result = wiki.getMoveListFor(charName)) {
            is Result.Success -> {
                Result.Success(result.data.toDomain())
            }
            is Result.Error -> {
                Result.Error(result.error.toDomainError())
            }
        }
    }
}