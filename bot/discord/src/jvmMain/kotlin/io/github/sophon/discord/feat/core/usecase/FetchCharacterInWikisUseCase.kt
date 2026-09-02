package io.github.sophon.discord.feat.core.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput

@ExcludeFromCoverage("plain client call")
internal class FetchCharacterInWikisUseCase {
    suspend operator fun invoke(
        wikis: Map<Game, WikiClient>,
        query: String,
        searchFun: suspend (Game, WikiClient, String) -> Result<BotOutput, BotError>,
    ): Result<BotOutput, BotError> {
        var lastError: BotError? = null
        for ((game, wiki) in wikis) {
            when (val result = searchFun(game, wiki, query)) {
                is Result.Success -> return result
                is Result.Error -> lastError = result.error
            }
        }

        val fallback = Result.Error(lastError ?: BotError.UnknownCharacter(query))
        return fallback
    }
}
