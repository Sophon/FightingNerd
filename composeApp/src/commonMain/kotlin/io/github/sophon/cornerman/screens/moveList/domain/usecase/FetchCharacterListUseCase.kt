package io.github.sophon.cornerman.screens.moveList.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.cornerman.screens.moveList.domain.MoveListError
import io.github.sophon.cornerman.screens.moveList.domain.toDomain
import com.example.wikiwavu.WavuWikiClient
import com.example.wikiwavu.domain.model.Character

class FetchCharacterListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): Result<List<Character>, MoveListError> {
        return when(val result = wiki.getCharacterList()) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.error.toDomain())
        }
    }
}