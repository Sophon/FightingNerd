package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.app.domain.model.FrameRange
import io.github.sophon.wiki.model.wiki.CoreFilters
import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game

internal fun DiscordConfig.toWikiConfig(): Result<WikiConfig, WikiError> {
    val gameSet = this.featureList
        .filter { it.isEnabled }
        .flatMap { it.supportedGames }
        .mapNotNull { gameId -> Game.fromId(gameId) }
        .toSet()

    val result = WikiConfig.create(
        availableGameSet = gameSet,
        enabledGameSet = gameSet,
    )

    return result
}

internal fun WikiError.toDomainError(): BotError {
    val botError = when (this) {
        is WikiError.UnknownCharacter -> BotError.UnknownCharacter(inputs[0])
        is WikiError.UnknownMove -> BotError.UnknownMove(*inputs)

        is WikiError.DownloadError,
        is WikiError.PageNotFound,
        is WikiError.DatabaseError,
        is WikiError.InvalidConfig -> BotError.WikiError(this.toString())
    }

    return botError
}

internal fun FrameRange.toFilter(): Filter {
    val filter = when (type) {
        FrameRange.Type.STARTUP -> CoreFilters.Startup(from = from, to = to)
        FrameRange.Type.ON_HIT -> CoreFilters.OnHit(from = from, to = to)
        FrameRange.Type.ON_BLOCK -> CoreFilters.OnBlock(from = from, to = to)
        FrameRange.Type.ON_COUNTER -> CoreFilters.OnCounter(from = from, to = to)
    }

    return filter
}
