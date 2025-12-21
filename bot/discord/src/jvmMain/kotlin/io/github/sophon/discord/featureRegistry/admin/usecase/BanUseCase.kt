package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.mapError
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.toDomainError
import io.github.sophon.domain.Source
import io.github.sophon.domain.model.Ban

internal class BanUseCase(
    private val adminTool: AdminTool,
) {
    suspend fun invoke(source: Source, offenderId: String): Result<Ban, BotError> {
        return adminTool.banUser(source, offenderId)
            .mapError { it.toDomainError() }
    }
}