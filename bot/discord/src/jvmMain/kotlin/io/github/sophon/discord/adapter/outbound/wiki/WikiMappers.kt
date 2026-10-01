package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.util.getGame
import io.github.sophon.core.util.orDash
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError

internal fun DiscordConfig.toWikiConfig(): Result<WikiConfig, WikiError> {
    val gameSet = this.featureList
        .filter { it.isEnabled }
        .flatMap { it.supportedGames }
        .mapNotNull { gameId -> gameId.getGame() }
        .toSet()

    val result = WikiConfig.create(
        availableGameSet = gameSet,
        enabledGameSet = gameSet,
    )

    return result
}

internal fun WikiError.toDomainError(): BotError {
    val botError = when (this) {
        is WikiError.UnknownCharacter,
        is WikiError.UnknownMove -> BotError.BotLogicError(this.toString())

        is WikiError.DownloadError,
        is WikiError.PageNotFound,
        is WikiError.DatabaseError,
        is WikiError.InvalidConfig -> BotError.WikiError(this.toString())
    }

    return botError
}

internal fun Move.toDomain(character: Character): BotResponse.MoveResponse {
    val isCollapsedByDefault: Boolean = when (character.id.game) {
        Game.GGST,
        Game.StreetFighter6,
        Game.BBCF,
        Game.GBVSR -> true

        else -> false
    }

    val moveResponse = BotResponse.MoveResponse(
        input = input,
        url = urls.wikiUrl,
        characterName = character.displayName,
        moveName = name,
        characterImageUrl = character.images?.iconUrl,
        primaryFields = listOf(
            BotResponse.MoveResponse.Field("Startup", startup.orDash()),
            BotResponse.MoveResponse.Field("Hit", onHit.orDash()),
            BotResponse.MoveResponse.Field("Block", onBlock.orDash()),
            BotResponse.MoveResponse.Field("Counter", onCH.orDash()),
            BotResponse.MoveResponse.Field("Damage", damage.orDash()),
        ),
        dataSource = BotResponse.DataSource(
            name = "${character.id.game.displayName} (${character.id.game.wiki.id})",
            iconUrl = character.id.game.iconUrl, //TODO: should be wiki icon url
        ),
        isCollapsedByDefault = isCollapsedByDefault,
        secondaryFields = listOfNotNull(
            guard?.let { BotResponse.MoveResponse.Field("Guard", it) },
            active?.let { BotResponse.MoveResponse.Field("Active", it) },
            cancel?.let { BotResponse.MoveResponse.Field("Cancel", it) },
            recovery?.let { BotResponse.MoveResponse.Field("Recovery", it) },
            invulnerability?.let { BotResponse.MoveResponse.Field("Invulnerability", it) },
        ),
        noteList = notes,
        aliasList = aliases,
        videoUrl = urls.videoUrl,
        hitboxImageList = urls.hitboxImageList,
    )

    return moveResponse
}
