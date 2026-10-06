package io.github.sophon.discord.app.model.response

import io.github.sophon.wiki.model.wiki.Game

sealed interface AliasResponse: BotResponse {
    data class CharacterAliases(
        val characterList: List<CharacterResponse>,
    ): AliasResponse {
        override val game: Game? get() = characterList.firstOrNull()?.game
    }

    data class GamePrompt(
        val gameList: List<String>,
        val buttonSet: BotResponse.ButtonSet,
    ): AliasResponse
}
