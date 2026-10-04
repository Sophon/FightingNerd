package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.URL_INVITE
import io.github.sophon.discord.URL_REPO
import io.github.sophon.discord.URL_STEAM_LOBBY
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.app.domain.model.UserRequest
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.Command
import io.github.sophon.discord.app.domain.model.MoveType
import kotlin.time.Duration.Companion.seconds

internal class CommandRouterService(
    private val moveService: MoveService,
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
            Command.Fd -> moveService.findFrameData(query)

            Command.Pc -> moveService.findMovesOfType(characterQuery = query, moveType = MoveType.PC)
            Command.Heat -> moveService.findMovesOfType(characterQuery = query, moveType = MoveType.HEAT)
            Command.Homing -> moveService.findMovesOfType(characterQuery = query, moveType = MoveType.HOMING)
            Command.Stance -> moveService.findStanceOrMove(query)
            Command.Strings -> moveService.findStrings(query)

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

            Command.Modules,
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

    private fun resolveCommand(userRequest: UserRequest): Command {
        val command = if (userRequest.query.startsWith(URL_STEAM_LOBBY, ignoreCase = true)) {
            Command.Join
        } else {
            userRequest.command ?: Command.Fd
        }

        return command
    }
}
