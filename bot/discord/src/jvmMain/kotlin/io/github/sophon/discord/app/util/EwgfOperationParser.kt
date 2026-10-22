package io.github.sophon.discord.app.util

import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.EwgfOperation

/**
 * Query is `[alias] [polarisId]`, a Discord mention, or blank for the caller's own data.
 */
internal fun String.toEwgfOperation(): Result<EwgfOperation, BotError> {
    if (isBlank()) return Result.Success(EwgfOperation.Data)
    extractTag()?.let { discordId ->
        return Result.Success(EwgfOperation.Search(discordId))
    }

    val parts = split(' ')
    val operation = findOperation(
        alias = parts.first(),
        data = parts.last(),
    ) ?: return Result.Error(BotError.SyntaxError(this))

    return Result.Success(operation)
}


private fun findOperation(alias: String, data: String): EwgfOperation? {
    val operation = operationDictionary
        .firstOrNull { (aliasList, _) -> alias in aliasList }
        ?.second
        ?.invoke(data)
    return operation
}

private fun String.extractTag(): String? {
    if (startsWith("<@").not() || endsWith(">").not()) return null

    val id = removePrefix("<@").removeSuffix(">")
    val isValidId = (id.length >= DISCORD_ID_MIN_LENGTH) && id.all { it.isDigit() }

    return if (isValidId) id else null
}


private const val DISCORD_ID_MIN_LENGTH = 17
private val operationDictionary = listOf(
    listOf("?", "help") to { _: String -> EwgfOperation.Help },
    listOf("+", "register") to { polarisId: String -> EwgfOperation.Register(polarisId) },
    listOf("update") to { polarisId: String -> EwgfOperation.Update(polarisId) },
    listOf("-", "unregister") to { _: String -> EwgfOperation.Unregister },
)
