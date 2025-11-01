package io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase

import com.example.core.domain.Result
import io.github.sophon.cornerman.screens.home.HomeError
import com.example.wikiwavu.WavuError
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
                val error = when (result.error) {
                    WavuError.UNKNOWN_CHARACTER -> HomeError.UNKNOWN_CHARACTER
                    WavuError.DOWNLOAD_ERROR -> HomeError.DOWNLOAD_ERROR
                    WavuError.CHARACTER_LIST_NOT_FOUND -> HomeError.CHARACTER_LIST_NOT_FOUND
                    WavuError.CHARACTER_SERIALIZATION_ERROR -> HomeError.CHARACTER_SERIALIZATION_ERROR
                    else -> HomeError.UNKNOWN
                }
                Result.Error(error) //TODO: map function
            }
        }
    }
}