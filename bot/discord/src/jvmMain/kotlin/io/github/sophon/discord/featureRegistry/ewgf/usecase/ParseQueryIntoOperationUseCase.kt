package io.github.sophon.discord.featureRegistry.ewgf.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.ewgf.EwgfOperations

internal class ParseQueryIntoOperationUseCase {
    fun invoke(query: String): Result<EwgfOperations.Operation, BotError> {
        if (query.isBlank()) return Result.Success(EwgfOperations.Operation.Data)
        query.extractTag()?.let { discordId ->
            return Result.Success(EwgfOperations.Operation.Search(discordId))
        }

        val parts = query.split(' ')

        val operation = EwgfOperations.findOperation(
            alias = parts.first(),
            data = parts.last(),
        ) ?: return Result.Error(BotError.SyntaxError(query))

        return Result.Success(operation)
    }

    private fun String.extractTag(): String? {
        if (startsWith("<@").not() || endsWith(">").not()) return null

        val id = removePrefix("<@").removeSuffix(">")
        val isValidId = (id.length >= 17) && id.all { it.isDigit() }

        return if (isValidId) id else null
    }
}
