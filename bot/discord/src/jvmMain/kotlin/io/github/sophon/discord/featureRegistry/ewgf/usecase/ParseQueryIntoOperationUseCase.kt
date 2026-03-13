package io.github.sophon.discord.featureRegistry.ewgf.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.ewgf.EwgfOperations

internal class ParseQueryIntoOperationUseCase {
    fun invoke(query: String): Result<EwgfOperations.Operation, BotError> {
        if (query.isBlank()) return Result.Success(EwgfOperations.Operation.Data)

        val parts = query.split(' ')

        val operation = EwgfOperations.findOperation(
            alias = parts.first(),
            data = parts.last(),
        ) ?: return Result.Error(BotError.SyntaxError(query))

        return Result.Success(operation)
    }
}
