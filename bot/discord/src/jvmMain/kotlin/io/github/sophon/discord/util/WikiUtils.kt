package io.github.sophon.discord.util

import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.model.BotOutput

internal suspend fun withWiki(
    wikis: Map<String, WikiClient>,
    gameId: String,
    query: String,
    action: suspend (String, WikiClient, String) -> Result<BotOutput, BotError>,
): Result<BotOutput, BotError> {
    return wikis[gameId]?.let { wiki ->
        action(gameId, wiki, query)
    } ?: Result.Error(BotError.UnsupportedGame(query))
}
