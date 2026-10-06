package io.github.sophon.discord.app.outPort

import io.github.sophon.discord.app.model.response.CharacterResponse

internal interface CharactersPort {
    suspend fun getCharacters(): List<CharacterResponse>
}
