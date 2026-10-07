package io.github.sophon.fightingnerd.adapter.outbound.wiki

import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.Character as WikiCharacter
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

internal fun Set<Game>.toWikiGameSet(): Set<WikiGame> {
    val wikiGameSet = this
        .mapNotNull { game -> WikiGame.fromId(game.id) }
        .toSet()
    return wikiGameSet
}

internal fun WikiCharacter.toDomain(): Character {
    val character = Character(
        id = id.naturalId,
        displayName = displayName,
        iconUrl = images?.iconUrl,
    )
    return character
}
