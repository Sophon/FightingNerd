package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.integration.AdminTool
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.integration.model.Source
import io.github.sophon.integration.util.toSource

@ExcludeFromCoverage("plain client call")
internal class UnbanUseCase(
    private val adminTool: AdminTool,
) {
    suspend operator fun invoke(origin: Source, query: String): Result<Source, BotError> {
        val target = query.toSource()
            ?: return Result.Error(BotError.InvalidQuery(query))

        return adminTool.unbanUser(origin = origin, offenderId = target.id)
            .map { target }
            .mapError { it.toDomainError() }
    }
}
