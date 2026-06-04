package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.feature.Game

internal interface GameWikiDiscordFeature {
    fun registerGames(enabledGames: List<Game>)
}
