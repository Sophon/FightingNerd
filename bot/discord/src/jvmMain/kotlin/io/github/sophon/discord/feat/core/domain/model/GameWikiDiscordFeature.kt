package io.github.sophon.discord.feat.core.domain.model

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.core.wiki.model.WikiClient

internal interface GameWikiDiscordFeature {
    fun registerWikiClients(wikiClientMap: Map<Game, WikiClient>)
    suspend fun getCharacterList(command: Command): Result<List<Character>, BotError>
    suspend fun getMoveList(command: Command, characterId: String): Result<List<Move>, BotError>
}
