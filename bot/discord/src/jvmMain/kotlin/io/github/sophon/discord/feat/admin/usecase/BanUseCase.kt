package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.integration.AdminTool
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.integration.model.Source
import io.github.sophon.integration.model.Ban
import io.github.sophon.integration.util.toSource

@ExcludeFromCoverage("plain client call")
internal class BanUseCase(
    private val adminTool: AdminTool,
) {
    suspend fun invoke(origin: Source, query: String): Result<Pair<Ban, Source>, BotError> {
        val target = query.toSource()
            ?: return Result.Error(BotError.InvalidQuery(query))

        return adminTool.banUser(origin = origin, offenderId = target.id)
            .map { ban -> Pair(ban, target) }
            .mapError { it.toDomainError() }
    }
}
