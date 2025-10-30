package com.example.cornerman.screens.moveList.domain.usecase

import com.example.core.domain.Result
import com.example.cornerman.screens.moveList.domain.MoveCategory
import com.example.cornerman.screens.moveList.domain.MoveListError
import com.example.cornerman.screens.moveList.domain.toDomain
import com.example.wikiwavu.WavuWikiClient

class FetchMoveListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(charName: String): Result<List<MoveCategory>, MoveListError> {
        return when (val result = wiki.getMoveListFor(charName)) {
            is Result.Success -> {
                Result.Success(result.data.toDomain())
            }
            is Result.Error -> {
                Result.Error(result.error.toDomain())
            }
        }
    }
}