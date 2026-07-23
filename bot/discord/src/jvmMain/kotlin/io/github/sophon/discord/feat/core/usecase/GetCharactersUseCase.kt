package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

@ExcludeFromCoverage("plain client call")
internal class GetCharactersUseCase {
    suspend fun invoke(wiki: WikiClient): Result<List<Character>, BotError> {
        return wiki.fetchCharacterList()
            .mapError { it.toDomainError() }
    }
}
