package io.github.sophon.discord.util

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput

internal suspend fun withWiki(
    wikis: Map<Game, WikiClient>,
    game: Game,
    query: String,
    action: suspend (Game, WikiClient, String) -> Result<BotOutput, BotError>,
): Result<BotOutput, BotError> {
    return wikis[game]?.let { wiki ->
        action(game, wiki, query)
    } ?: Result.Error(BotError.UnsupportedGame(query))
}
