package io.github.sophon.fightingnerd.adapter.outbound.wiki

import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.Character as WikiCharacter
import io.github.sophon.wiki.model.Move as WikiMove
import io.github.sophon.wiki.model.RefreshEvent as WikiRefreshEvent
import io.github.sophon.wiki.model.wiki.Game as WikiGame

internal fun WikiRefreshEvent.toDomain(): RefreshEvent {
    val refreshEvent = when (this) {
        is WikiRefreshEvent.Failed -> RefreshEvent.Failed(error.toDomainError())
        is WikiRefreshEvent.Finished -> RefreshEvent.Finished(successCount)
    }
    return refreshEvent
}

internal fun WikiError.toDomainError(): AppError {
    val appError = AppError.WikiError(this.toString())
    return appError
}

internal fun WikiGame.toDomain(): Game {
    val game = Game(
        id = id,
        displayName = displayName,
        iconUrl = iconUrl,
        wikiName = wiki.displayName,
    )
    return game
}

internal fun ComposeConfig.toWikiConfig(enabledGameIdSet: Set<String>): Result<WikiConfig, WikiError> {
    val availableGameSet = availableFeatureList
        .flatMap { feature -> feature.supportedGames }
        .toWikiGameSet()
    val enabledGameSet = enabledGameIdSet.toWikiGameSet()

    val result = WikiConfig.create(
        availableGameSet = availableGameSet,
        enabledGameSet = enabledGameSet,
    )
    return result
}

private fun Iterable<String>.toWikiGameSet(): Set<WikiGame> {
    val wikiGameSet = this
        .mapNotNull { gameId -> WikiGame.fromId(gameId) }
        .toSet()
    return wikiGameSet
}

internal fun WikiCharacter.toDomain(): Character {
    val character = Character(
        id = id.naturalId,
        displayName = displayName,
        iconUrl = images?.iconUrl,
        hp = hp,
        umo = umo,
        gameProperties = gameProperties?.toDomain(),
    )
    return character
}

internal fun WikiMove.toDomain(
    groupId: String,
    filterNameSet: Set<String>,
): Move {
    val move = Move(
        input = input,
        remoteId = remoteId,
        name = name,
        damage = damage,
        startup = startup,
        onBlock = onBlock,
        onHit = onHit,
        onCH = onCH,
        active = active,
        cancel = cancel,
        recovery = recovery,
        guard = guard,
        invulnerability = invulnerability,
        isThrow = isThrow,
        type = type,
        notes = notes,
        aliases = aliases,
        urls = urls.toDomain(),
        gameProperties = gameProperties?.toDomain(),
        groupId = groupId,
        filterNameSet = filterNameSet,
    )
    return move
}

private fun WikiMove.Urls.toDomain(): Move.Urls {
    val urls = Move.Urls(
        wikiUrl = wikiUrl,
        videoId = videoId,
        videoUrl = videoUrl,
        hitboxImageList = hitboxImageList,
        moveImageList = moveImageList,
    )
    return urls
}
