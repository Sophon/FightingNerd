package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput

internal class FetchMoveInWikisUseCase {
    suspend fun invoke(
        wikis: Map<String, WikiClient>,
        query: String,
        searchFun: suspend (String, WikiClient, String) -> Result<BotOutput, BotError>,
    ): Result<BotOutput, BotError> {
        var lastError: BotError? = null
        for ((gameId, wiki) in wikis) {
            when (val result = searchFun(gameId, wiki, query)) {
                is Result.Success -> return result
                is Result.Error -> lastError = result.error
            }
        }

        return Result.Error(lastError ?: BotError.UnknownMove(query))
    }
}