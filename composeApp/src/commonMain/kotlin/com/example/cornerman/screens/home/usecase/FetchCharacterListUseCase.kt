package com.example.cornerman.screens.home.usecase

import com.example.core.domain.Result
import com.example.cornerman.screens.home.domain.HomeError
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Character

class FetchCharacterListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): Result<List<Character>, HomeError> {
        return when (val result = wiki.getCharacterList()) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> {
                Result.Error(HomeError.UNKNOWN) //TODO: map properly
            }
        }
    }
}