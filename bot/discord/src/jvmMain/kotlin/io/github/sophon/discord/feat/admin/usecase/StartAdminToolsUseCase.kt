package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.AdminTool
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.feature.Config
import io.github.sophon.discord.domain.model.BotError
import io.github.sophon.discord.domain.toDomainError

internal class StartAdminToolsUseCase(
    private val adminTool: AdminTool,
) {
    fun invoke(adminConfig: Config.AdminConfig): EmptyResult<BotError> {
        return adminTool.init(adminConfig)
            .mapError { it.toDomainError() }
    }
}