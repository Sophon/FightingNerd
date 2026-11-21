package io.github.sophon.discord.app.model

import io.github.sophon.core.architecture.Error

sealed class BotError(private vararg val inputs: String) : Error {
    class InvalidCommand(val command: String): BotError(command)
    class NotImplemented(command: String): BotError(command)
    class InvalidQuery(input: String) : BotError(input)
    class InvalidSteamLobbyUrl(steamLobbyUrl: String): BotError(steamLobbyUrl)
    class BotLogicError(vararg inputs: String) : BotError(*inputs)
    class Kord(error: String): BotError(error)

    class ConfigError(error: String): BotError(error)

    class AdminError(error: String): BotError(error)
    class PermissionDenied : BotError()
    class UserBanned(userId: String) : BotError(userId)

    class WikiError(error: String): BotError(error)
    class UnknownCharacter(input: String) : BotError(input)
    class UnknownMove(vararg inputs: String) : BotError(*inputs)
    class UnsupportedGame(vararg inputs: String): BotError(*inputs)

    class GlossaryTermNotFound(input: String) : BotError(input)
    class EmptyGlossary : BotError()

    class PlayerNotRegistered : BotError()
    class SyntaxError(input: String): BotError(input)

    class DownloadError(input: String) : BotError(input)
    class FileError(vararg errors: String) : BotError(*errors)
    class DatabaseError : BotError()

    class Unknown(input: String = "") : BotError(input)

    override fun toString(): String =
        "${this::class.simpleName}(${inputs.joinToString()})"
}
