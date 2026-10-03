package io.github.sophon.discord.app.port.outbound

import io.github.sophon.discord.app.domain.model.BotResponse

internal interface CharactersPort {
    suspend fun getCharacters(): List<BotResponse.CharacterResponse>
}
