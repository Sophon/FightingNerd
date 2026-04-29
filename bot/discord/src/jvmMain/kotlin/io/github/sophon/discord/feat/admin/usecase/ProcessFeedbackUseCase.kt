package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source

internal class ProcessFeedbackUseCase(
    private val adminTool: AdminTool,
) {
    suspend fun invoke(origin: Source, message: String): Result<AdminResult, BotError> {
        return adminTool.processFeedback(
            origin = origin,
            feedback = message,
        ).mapError { it.toDomainError() }
    }
}