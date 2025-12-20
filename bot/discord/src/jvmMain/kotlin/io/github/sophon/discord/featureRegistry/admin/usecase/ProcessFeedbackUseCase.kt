package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.AdminResult

internal class ProcessFeedbackUseCase(
    private val adminTool: AdminTool,
) {
    suspend fun invoke(authorId: String, message: String): Result<AdminResult, BotError> {
        return adminTool.processFeedback(authorId, message)
            .mapError { it.toDomainError() }
    }
}