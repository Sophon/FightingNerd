package io.github.sophon.discord.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.toDomainError

internal class GetCharactersUseCase {
    suspend fun invoke(wiki: WikiClient): Result<List<Character>, BotError> {
        return wiki.fetchCharacterList()
            .mapError { it.toDomainError() }
    }
}