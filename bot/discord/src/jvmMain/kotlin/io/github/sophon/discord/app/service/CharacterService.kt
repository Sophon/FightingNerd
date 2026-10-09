package io.github.sophon.discord.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.util.equalsIgnoreCase
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.GameList
import io.github.sophon.discord.app.model.discord.Command
import io.github.sophon.discord.app.model.frameData.CharacterId
import io.github.sophon.discord.app.model.response.AliasResponse
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.CharacterResponse
import io.github.sophon.discord.app.outPort.CharactersPort
import io.github.sophon.discord.app.outPort.GamePort
import io.github.sophon.wiki.model.wiki.Game
import kotlin.time.Duration.Companion.seconds

internal interface CharacterService {
    suspend fun findCharacter(
        characterQuery: String,
        requireProperties: Boolean = false,
    ): Result<CharacterResponse, BotError>

    suspend fun findCharacter(characterId: CharacterId): Result<CharacterResponse, BotError>

    suspend fun findAliases(gameQuery: String): Result<AliasResponse, BotError>
    suspend fun getCharacters(): List<CharacterResponse>
}

internal class CharacterServiceImpl(
    private val charactersPort: CharactersPort,
    private val gamePort: GamePort,
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

    override suspend fun findCharacter(characterId: CharacterId): Result<CharacterResponse, BotError> {
        val character = getCharacters()
            .firstOrNull { it.game == characterId.game && it.id == characterId.characterId }
        val result = if (character != null) {
            Result.Success(character)
        } else {
            Result.Error(BotError.UnknownCharacter(characterId.characterId))
        }

        return result
    }

    /**
     * Blank query -> list of available games + buttons
     */
    override suspend fun findAliases(gameQuery: String): Result<AliasResponse, BotError> {
        val result = if (gameQuery.isBlank()) {
            Result.Success(createGamePromptResponse(gamePort.getGameList()))
        } else {
            val game = Game.fromId(gameQuery)
            val gameCharacterList = getCharacters().filter { it.game == game }
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

    private fun createGamePromptResponse(gameList: GameList): AliasResponse {
        val response = AliasResponse.GamePrompt(
            gameList = gameList.gameList.map { it.displayName },
            dataSource = gameList.dataSource,
            buttonSet = BotResponse.ButtonSet(
                buttonList = gameList.gameList.mapIndexed { index, game ->
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
