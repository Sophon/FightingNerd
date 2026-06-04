package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.WikiClient

internal interface GameWikiDiscordFeature {
    fun registerWikiClients(wikiClientMap: Map<Game, WikiClient>)
}
