package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source

internal class ReplyToFeedbackUseCase(
    private val adminTool: AdminTool,
) {
    fun invoke(source: Source, reply: String): Result<AdminResult, BotError> {
        return adminTool.replyToFeedback(source, reply)
            .mapError { it.toDomainError() }
    }
}