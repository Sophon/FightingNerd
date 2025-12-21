package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.Source
import io.github.sophon.domain.model.Ban
import io.github.sophon.util.toSourceAndMessage

internal class BanUseCase(
    private val adminTool: AdminTool,
) {
    suspend fun invoke(origin: Source, query: String): Result<Pair<Ban, Source>, BotError> {
        val (target, _) = query.toSourceAndMessage()
            ?: return Result.Error(BotError.InvalidQuery(query))

        return adminTool.banUser(origin = origin, offenderId = target.id)
            .map { ban -> Pair(ban, target) }
            .mapError { it.toDomainError() }
    }
}
