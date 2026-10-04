package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.CharacterId as WikiCharacterId

internal fun Character.toDomain(): BotResponse.CharacterResponse {
    val characterResponse = BotResponse.CharacterResponse(
        id = id.naturalId,
        game = id.game,
        displayName = displayName,
        dataSource = toDataSource(),
        aliasList = aliasList,
    )

    return characterResponse
}

internal fun CharacterId.toWikiCharacterId(): WikiCharacterId {
    val wikiCharacterId = WikiCharacterId(
        game = game,
        naturalId = characterId,
    )

    return wikiCharacterId
}

internal fun Character.toDataSource(): BotResponse.DataSource {
    val dataSource = BotResponse.DataSource(
        name = "${id.game.displayName} (${id.game.wiki.displayName})",
        iconUrl = id.game.wiki.iconUrl,
    )

    return dataSource
}
