package io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.cornerman.screens.home.HomeError
import io.github.sophon.cornerman.screens.home.toDomain
import io.github.sophon.wikiwavu.WavuWikiClient
import io.github.sophon.wikiwavu.domain.model.Character

internal class FetchCharacterListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): Result<List<Character>, HomeError> {
        return when (val result = wiki.getCharacterList()) {
            is Result.Success -> {
                Result.Success(result.data)
            }
            is Result.Error -> Result.Error(result.error.toDomain())
        }
    }
}