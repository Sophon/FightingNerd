package com.example.cornerman.moveList.useCase

import com.example.core.domain.Result
import com.example.cornerman.moveList.MoveListError
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Character
import com.example.wikiwavu.domain.model.CharacterMoveList

class FetchMoveListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(character: Character): Result<CharacterMoveList, MoveListError> {
        return when (val result = wiki.getMoveListFor(character)) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(MoveListError.UNKNOWN) //TODO: mapper
            }
        }
    }
}