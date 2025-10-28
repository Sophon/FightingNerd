package com.example.cornerman.screens.moveList.useCase

import com.example.core.domain.Result
import com.example.cornerman.screens.moveList.MoveListError
import com.example.cornerman.screens.moveList.mapper.toDomain
import com.example.cornerman.screens.moveList.model.MoveCategory
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Character

class FetchMoveListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(character: Character): Result<List<MoveCategory>, MoveListError> {
        return when (val result = wiki.getMoveListFor(character)) {
            is Result.Success -> {
                Result.Success(result.data.toDomain())
            }
            is Result.Error -> {
                Result.Error(MoveListError.UNKNOWN) //TODO: mapper
            }
        }
    }
}

