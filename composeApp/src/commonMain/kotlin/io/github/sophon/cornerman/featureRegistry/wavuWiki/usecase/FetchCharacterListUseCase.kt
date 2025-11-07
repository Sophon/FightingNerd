package io.github.sophon.cornerman.featureRegistry.wavuWiki.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.model.Character
import io.github.sophon.cornerman.screens.home.HomeError
import io.github.sophon.cornerman.screens.home.toDomain
import io.github.sophon.wikiwavu.WavuWikiClient

internal class FetchCharacterListUseCase(
    private val wiki: WavuWikiClient,
) {
    suspend fun invoke(): Result<List<Character>, HomeError> {
        return wiki.getCharacterList().mapError { it.toDomain() }
    }
}