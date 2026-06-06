package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.integration.AdminTool
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.integration.model.AdminResult
import io.github.sophon.integration.model.Source

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