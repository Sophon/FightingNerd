package io.github.sophon.discord.domain.model

import io.github.sophon.core.domain.Error

sealed class BotError(private vararg val inputs: String) : Error {
    class InvalidCommand(val command: String): BotError(command)
    class InvalidQuery(input: String) : BotError(input)
    class UnknownCharacter(input: String) : BotError(input)
    class UnknownMove(vararg inputs: String) : BotError(*inputs)
    class GlossaryTermNotFound(input: String) : BotError(input)
    class DownloadError(input: String) : BotError(input)
    class BotLogicError(vararg inputs: String) : BotError(*inputs)
    class UnsupportedGame(vararg inputs: String): BotError(*inputs)
    class Kord(error: String): BotError(error)
    class InvalidSteamLobbyUrl(steamLobbyUrl: String): BotError(steamLobbyUrl)
    class SyntaxError(input: String): BotError(input)
    class PlayerNotRegistered : BotError()

    class FileError(vararg errors: String) : BotError(*errors)
    class DatabaseError : BotError()

    class Unknown(input: String = "") : BotError(input)

    override fun toString(): String =
        "${this::class.simpleName}(${inputs.joinToString()})"
}