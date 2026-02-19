package io.github.sophon.discord

import io.github.sophon.core.domain.Error

sealed class BotError(private vararg val inputs: String) : Error {
    class InvalidQuery(input: String) : BotError(input)
    class UnknownCharacter(input: String) : BotError(input)
    class UnknownMove(vararg inputs: String) : BotError(*inputs)
    class GlossaryTermNotFound(input: String) : BotError(input)
    class DownloadError(input: String) : BotError(input)
    class BotLogicError(vararg inputs: String) : BotError(*inputs)
    class EmptyDatabase : BotError()
    class UnsupportedGame(vararg inputs: String): BotError(*inputs)
    class Kord(error: String): BotError(error)
    class InvalidSteamLobbyUrl(steamLobbyUrl: String): BotError(steamLobbyUrl)
    class SyntaxError(input: String): BotError(input)
    class PlayerNotRegistered : BotError()

    class Unknown(input: String = "") : BotError(input)

    override fun toString(): String =
        "${this::class.simpleName}(${inputs.joinToString()})"
}
