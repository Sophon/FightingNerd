package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class SyncWikiDataUseCase {
    suspend fun invoke(wikiList: Collection<WikiClient>): EmptyResult<BotError> {
        val errors = mutableListOf<BotError>()

        for (wiki in wikiList) {
            wiki.refreshData().onError {
                errors.add(it.toDomainError())
            }
        }

        val result = if (errors.isEmpty()) {
            Result.Success(Unit)
        } else {
            Result.Error(errors.first())
        }
        return result
    }
}
