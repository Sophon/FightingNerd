package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.discord.app.domain.model.UserRequest

internal class CommandRouterService(
    private val moveService: MoveService,
    private val characterService: CharacterService,
    private val coreBotService: CoreBotService,
    private val banService: BanService,
    private val adminService: AdminService,
) {
    suspend operator fun invoke(userRequest: UserRequest): Result<BotResponse, BotError> {
        val initialResult = invoke(
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

    suspend operator fun invoke(
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
            Command.Startup,
            Command.OnHit,
            Command.OnBlock,
            Command.OnCounter -> moveService.findMovesInRange(query = query, command = command)

            Command.Char -> characterService.findCharacter(characterQuery = query, requireProperties = true)
            Command.Alias -> characterService.findAliases(gameQuery = query)

            Command.Tip,
            Command.Donate -> coreBotService.createTipResponse()
            Command.Help -> coreBotService.createHelpResponse()
            Command.Commands -> coreBotService.createCommandsResponse()
            Command.Repo -> coreBotService.createRepoResponse()
            Command.Invite -> coreBotService.createInviteResponse()
            Command.Modules -> coreBotService.createModulesResponse()
            Command.Join -> coreBotService.createSteamLobbyResponse(query = query, source = source)

            Command.Ban -> banService.ban(query = query, source = source)
            Command.Unban -> banService.unban(query = query, source = source)

            Command.Feedback -> adminService.forwardFeedback(query = query, source = source)
            Command.Reply -> adminService.replyToFeedback(query = query, source = source)

            Command.Banlist,
            Command.Refresh,
            Command.Gl,
            Command.ThrowTK,
            Command.SpecialROA,
            Command.Ewgf -> Result.Error(BotError.NotImplemented(command.name))
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

        val result = invoke(
            command = command,
            query = (wordList - commandWord).joinToString(" "),
            source = source,
        )
        return result
    }
}
