package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.port.outbound.CharactersPort

internal interface CharacterService {
    suspend fun findCharacter(characterQuery: String): Result<BotResponse.CharacterResponse, BotError>
    suspend fun getCharacters(): List<BotResponse.CharacterResponse>
}

internal class CharacterServiceImpl(
    private val charactersPort: CharactersPort,
): CharacterService {
    override suspend fun findCharacter(
        characterQuery: String,
    ): Result<BotResponse.CharacterResponse, BotError> {
        val character = charactersPort.getCharacters().firstOrNull { it.matches(characterQuery) }
        val result = if (character != null) {
            Result.Success(character)
        } else {
            Result.Error(BotError.UnknownCharacter(characterQuery))
        }

        return result
    }

    override suspend fun getCharacters(): List<BotResponse.CharacterResponse> {
        val characterList = charactersPort.getCharacters()
        return characterList
    }
}

private fun BotResponse.CharacterResponse.matches(characterQuery: String): Boolean {
    return id.equalsIgnoreCase(characterQuery)
            || aliasList.any { it.equalsIgnoreCase(characterQuery) }
}
