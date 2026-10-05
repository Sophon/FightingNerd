package io.github.sophon.discord.app.outPort

import io.github.sophon.discord.app.model.BotResponse

internal interface CharactersPort {
    suspend fun getCharacters(): List<BotResponse.CharacterResponse>
}
