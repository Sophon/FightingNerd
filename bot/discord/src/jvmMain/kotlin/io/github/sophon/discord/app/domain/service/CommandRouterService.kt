package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.port.outbound.FrameDataPort
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.MoveType
import io.github.sophon.discord.app.port.outbound.GetMovesOfTypePort

internal class CommandRouterService(
    private val frameDataPort: FrameDataPort,
    private val getMovesOfTypePort: GetMovesOfTypePort,
) {
    suspend operator fun invoke(userRequest: UserRequest): Result<BotResponse, BotError> {
        val initialResult = route(
            command = resolveCommand(userRequest),
            query = userRequest.query,
        )

        val result = if ((userRequest.command == null) && (initialResult is Result.Error)) {
            retryWithExtractedCommand(query = userRequest.query, originalResult = initialResult)
        } else {
            initialResult
        }

        return result
    }

    suspend fun route(command: Command, query: String): Result<BotResponse, BotError> {
        val result = when (command) {
            Command.Fd -> frameDataPort.getFrameData(query)

            Command.Pc -> getMovesOfTypePort.getMovesOfType(characterQuery = query, moveType = MoveType.PC)
            Command.Heat -> getMovesOfTypePort.getMovesOfType(characterQuery = query, moveType = MoveType.HEAT)
            Command.Homing -> getMovesOfTypePort.getMovesOfType(characterQuery = query, moveType = MoveType.HOMING)
            Command.Stance -> {
                val characterQuery = query.substringBefore(' ')
                val stanceQuery = query.substringAfter(' ', missingDelimiterValue = "").trim()
                if (stanceQuery.isEmpty()) {
                    getMovesOfTypePort.getStances(characterQuery = characterQuery)
                } else {
                    getMovesOfTypePort.getStanceMoves(characterQuery = characterQuery, stanceQuery = stanceQuery)
                }
            }

            Command.Tip,
            Command.Donate,
            Command.Repo,
            Command.Invite,
            Command.Help,
            Command.Modules,
            Command.Commands,
            Command.Join,
            Command.Feedback,
            Command.Reply,
            Command.Ban,
            Command.Unban,
            Command.Banlist,
            Command.Refresh,
            Command.Char,
            Command.Alias,
            Command.Startup,
            Command.OnHit,
            Command.OnBlock,
            Command.OnCounter,
            Command.Gl,
            Command.ThrowTK,
            Command.Strings,
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
        originalResult: Result<BotResponse, BotError>,
    ): Result<BotResponse, BotError> {
        val wordList = query.split(' ')
        val (commandWord, command) = wordList
            .firstNotNullOfOrNull { word -> Command.fromId(word)?.let { word to it } }
            ?: return originalResult

        val result = route(
            command = command,
            query = (wordList - commandWord).joinToString(" "),
        )
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
