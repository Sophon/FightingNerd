package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.model.AdminResult
import io.github.sophon.model.Source
import io.github.sophon.app.util.toSourceAndMessage

@ExcludeFromCoverage("plain client call")
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
