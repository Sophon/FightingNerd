package io.github.sophon.cornerman.screens.moveList.domain.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.cornerman.screens.moveList.domain.MoveListError
import io.github.sophon.cornerman.screens.moveList.domain.toDomain
import io.github.sophon.wikiwavu.WavuWikiClient

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