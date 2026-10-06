package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.discord.BOT_DATA_SOURCE
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.model.Command
import io.github.sophon.discord.app.outPort.CharactersPort
import io.github.sophon.wiki.model.wiki.Game
import kotlin.time.Duration.Companion.seconds

internal interface CharacterService {
    suspend fun findCharacter(
        characterQuery: String,
        requireProperties: Boolean = false,
    ): Result<CharacterResponse, BotError>
    suspend fun findAliases(gameQuery: String): Result<AliasResponse, BotError>
    suspend fun getCharacters(): List<CharacterResponse>
}

internal class CharacterServiceImpl(
    private val charactersPort: CharactersPort,
): CharacterService {
    override suspend fun findCharacter(
        characterQuery: String,
        requireProperties: Boolean,
    ): Result<CharacterResponse, BotError> {
        val character = getCharacters()
            .filter { requireProperties.not() || it.propertyList.isNotEmpty() }
            .firstOrNull { it.matches(characterQuery) }
        val result = if (character != null) {
            Result.Success(character)
        } else {
            Result.Error(BotError.UnknownCharacter(characterQuery))
        }

        return result
    }

    /**
     * Blank query -> list of games with character properties + buttons
     */
    override suspend fun findAliases(gameQuery: String): Result<AliasResponse, BotError> {
        val characterList = getCharacters()

        val result = if (gameQuery.isBlank()) {
            Result.Success(createGamePromptResponse(gameList = characterList.map { it.game }.distinct()))
        } else {
            val game = Game.fromId(gameQuery)
            val gameCharacterList = characterList.filter { it.game == game }
            if (gameCharacterList.isEmpty()) {
                Result.Error(BotError.UnsupportedGame(gameQuery))
            } else {
                Result.Success(AliasResponse.CharacterAliases(characterList = gameCharacterList))
            }
        }
        return result
    }

    override suspend fun getCharacters(): List<CharacterResponse> {
        val characterList = charactersPort.getCharacters()
        return characterList
    }

    private fun createGamePromptResponse(gameList: List<Game>): AliasResponse {
        val response = AliasResponse.GamePrompt(
            gameList = gameList.map { it.displayName },
            dataSource = BOT_DATA_SOURCE,
            buttonSet = BotResponse.ButtonSet(
                buttonList = gameList.mapIndexed { index, game ->
                    BotResponse.EmbedButton(
                        label = (index + 1).toString(),
                        action = BotResponse.EmbedButton.Action.Command(command = Command.Alias, query = game.id),
                    )
                },
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            ),
        )
        return response
    }
}

private fun CharacterResponse.matches(characterQuery: String): Boolean {
    return id.equalsIgnoreCase(characterQuery)
            || aliasList.any { it.equalsIgnoreCase(characterQuery) }
}
