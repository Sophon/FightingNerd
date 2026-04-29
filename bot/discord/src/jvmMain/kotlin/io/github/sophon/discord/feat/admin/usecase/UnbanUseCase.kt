package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.domain.Source
import io.github.sophon.util.toSource

internal class UnbanUseCase(
    private val adminTool: AdminTool,
) {
    suspend fun invoke(origin: Source, query: String): Result<Source, BotError> {
        val target = query.toSource()
            ?: return Result.Error(BotError.InvalidQuery(query))

        return adminTool.unbanUser(origin = origin, offenderId = target.id)
            .map { target }
            .mapError { it.toDomainError() }
    }
}
