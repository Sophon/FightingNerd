package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.AdminResult
import io.github.sophon.util.toSourceAndMessage

internal class ReplyToFeedbackUseCase(
    private val adminTool: AdminTool,
) {
    fun invoke(query: String): Result<AdminResult, BotError> {
        val (target, reply) = query.toSourceAndMessage()
            ?: return Result.Error(BotError.InvalidQuery(query))

        return adminTool.replyToFeedback(target, reply)
            .mapError { it.toDomainError() }
    }
}
