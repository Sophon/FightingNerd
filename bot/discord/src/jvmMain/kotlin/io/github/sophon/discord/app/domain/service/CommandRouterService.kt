package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.URL_SCRIPT_LOBBY
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.port.outbound.LoadConfigPort
import io.github.sophon.wiki.application.domain.model.wiki.Game
import kotlin.time.Duration.Companion.seconds

internal class CommandRouterService(
    private val moveService: MoveService,
    private val characterService: CharacterService,
    private val loadConfigPort: LoadConfigPort,
) {
    suspend operator fun invoke(userRequest: UserRequest): Result<BotResponse, BotError> {
        val initialResult = route(
            command = resolveCommand(userRequest),
            query = userRequest.query,
            source = userRequest.source,
        )

        val result = if ((userRequest.command == null) && (initialResult is Result.Error)) {
            retryWithExtractedCommand(
                query = userRequest.query,
                source = userRequest.source,
                originalResult = initialResult,
            )
        } else {
            initialResult
        }

        return result
    }

    suspend fun route(
        command: Command,
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse, BotError> {
        val result = when (command) {
            Command.Fd -> moveService.findFrameData(query)

            Command.Pc -> moveService.findMovesOfType(characterQuery = query, moveType = MoveType.PC)
            Command.Heat -> moveService.findMovesOfType(characterQuery = query, moveType = MoveType.HEAT)
            Command.Homing -> moveService.findMovesOfType(characterQuery = query, moveType = MoveType.HOMING)
            Command.Stance -> moveService.findStanceOrMove(query)
            Command.Strings -> moveService.findStrings(query)

            Command.Char -> characterService.findCharacter(characterQuery = query, requireProperties = true)

            Command.Tip,
            Command.Donate -> Result.Success(BotResponse.CoreResponse(type = BotResponse.CoreResponse.Type.Tip))
            Command.Help -> {
                createLinkedCoreResponse(
                    type = BotResponse.CoreResponse.Type.Help,
                    linkedCommand = Command.Commands,
                )
            }
            Command.Commands -> {
                createLinkedCoreResponse(
                    type = BotResponse.CoreResponse.Type.Commands,
                    linkedCommand = Command.Help,
                )
            }
            Command.Repo -> Result.Success(BotResponse.PlainText(text = "Contribute to FightingNerd: $URL_REPO"))
            Command.Invite -> Result.Success(BotResponse.PlainText(text = "FightingNerd bot invite: $URL_INVITE"))

            Command.Modules -> createModulesResponse()
            Command.Alias -> createAliasResponse(gameQuery = query)

            Command.Join -> createSteamLobbyResponse(query = query, source = source)

            Command.Feedback,
            Command.Reply,
            Command.Ban,
            Command.Unban,
            Command.Banlist,
            Command.Refresh,
            Command.Startup,
            Command.OnHit,
            Command.OnBlock,
            Command.OnCounter,
            Command.Gl,
            Command.ThrowTK,
            Command.SpecialROA,
            Command.Ewgf -> Result.Error(BotError.NotImplemented(command.name))
        }

        return result
    }

    /**
     * Only for requests without an explicit command. Finds the first command anywhere in the query
     * and routes again with that command and the remaining words as the query.
     */
    private suspend fun retryWithExtractedCommand(
        query: String,
        source: UserRequest.Source,
        originalResult: Result<BotResponse, BotError>,
    ): Result<BotResponse, BotError> {
        val wordList = query.split(' ')
        val (commandWord, command) = wordList
            .firstNotNullOfOrNull { word -> Command.fromId(word)?.let { word to it } }
            ?: return originalResult

        val result = route(
            command = command,
            query = (wordList - commandWord).joinToString(" "),
            source = source,
        )
        return result
    }

    /**
     * Help and Commands point at each other with a button.
     */
    private fun createLinkedCoreResponse(
        type: BotResponse.CoreResponse.Type,
        linkedCommand: Command,
    ): Result<BotResponse, BotError> {
        val response = BotResponse.CoreResponse(
            type = type,
            buttonSet = BotResponse.ButtonSet(
                buttonList = listOf(
                    BotResponse.EmbedButton(
                        label = linkedCommand.name,
                        action = BotResponse.EmbedButton.Action.Command(command = linkedCommand, query = ""),
                    ),
                ),
                duration = EMBED_BUTTON_DURATION_INF.seconds,
            ),
        )
        return Result.Success(response)
    }

    /**
     * Enabled wikis with their enabled games, in config order.
     */
    private fun createModulesResponse(): Result<BotResponse, BotError> {
        val result = loadConfigPort.load()
            .map { discordConfig ->
                val moduleList = discordConfig.featureList
                    .filter { it.isEnabled }
                    .flatMap { it.supportedGames }
                    .mapNotNull { gameId -> Game.fromId(gameId) }
                    .groupBy { it.wiki }
                    .map { (wiki, gameList) ->
                        BotResponse.ModulesResponse.Module(
                            name = wiki.displayName,
                            url = wiki.url,
                            gameList = gameList.map { it.displayName },
                        )
                    }
                BotResponse.ModulesResponse(moduleList = moduleList)
            }
        return result
    }

    /**
     * Blank query prompts with a button per game that has characters.
     */
    private suspend fun createAliasResponse(gameQuery: String): Result<BotResponse, BotError> {
        val characterList = characterService.getCharacters()

        val result = if (gameQuery.isBlank()) {
            Result.Success(createGamePromptResponse(gameList = characterList.map { it.game }.distinct()))
        } else {
            val game = Game.fromId(gameQuery)
            val gameCharacterList = characterList.filter { it.game == game }
            if (gameCharacterList.isEmpty()) {
                Result.Error(BotError.UnsupportedGame(gameQuery))
            } else {
                Result.Success(BotResponse.AliasResponse.CharacterAliases(characterList = gameCharacterList))
            }
        }
        return result
    }

    private fun createGamePromptResponse(gameList: List<Game>): BotResponse {
        val response = BotResponse.AliasResponse.GamePrompt(
            gameList = gameList.map { it.displayName },
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

    /**
     * Query is `[steamLobbyUrl] [password] [lobbyName]`; the lobby name can have spaces.
     * Button paths have no [source] to name the host.
     */
    private fun createSteamLobbyResponse(
        query: String,
        source: UserRequest.Source?,
    ): Result<BotResponse, BotError> {
        val parts = query.split(" ")
        val steamLobbyUrl = parts[0]

        val result = when {
            (source == null) -> Result.Error(BotError.BotLogicError(Command.Join.name, query))
            (steamLobbyUrl.startsWith(URL_STEAM_LOBBY, ignoreCase = true).not()) -> {
                Result.Error(BotError.InvalidSteamLobbyUrl(steamLobbyUrl))
            }
            else -> {
                val joinUrl = "$URL_SCRIPT_LOBBY?target=$steamLobbyUrl"
                val response = BotResponse.SteamLobby(
                    hostName = source.username,
                    lobbyName = parts.drop(2).joinToString(" ").ifBlank { null },
                    password = parts.getOrNull(1),
                    buttonSet = BotResponse.ButtonSet(
                        buttonList = listOf(
                            BotResponse.EmbedButton(
                                label = "🎮 JOIN",
                                action = BotResponse.EmbedButton.Action.Url(joinUrl),
                            ),
                        ),
                        duration = EMBED_BUTTON_DURATION_INF.seconds,
                    ),
                )
                Result.Success(response)
            }
        }
        return result
    }

    private fun resolveCommand(userRequest: UserRequest): Command {
        val command = if (userRequest.query.startsWith(URL_STEAM_LOBBY, ignoreCase = true)) {
            Command.Join
        } else {
            userRequest.command ?: Command.Fd
        }

        return command
    }
}
