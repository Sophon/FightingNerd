package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source
import io.github.sophon.util.toSourceAndMessage

internal class ReplyToFeedbackUseCase(
    private val adminTool: AdminTool,
) {
    fun invoke(origin: Source, query: String): Result<AdminResult, BotError> {
        val (target, reply) = query.toSourceAndMessage()
            ?: return Result.Error(BotError.InvalidQuery(query))

        return adminTool.replyToFeedback(origin, target, reply)
            .mapError { it.toDomainError() }
    }
}
