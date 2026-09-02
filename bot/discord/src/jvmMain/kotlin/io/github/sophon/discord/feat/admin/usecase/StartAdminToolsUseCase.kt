package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.integration.AdminTool
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.toDomainError

@ExcludeFromCoverage("plain client call")
internal class StartAdminToolsUseCase(
    private val adminTool: AdminTool,
) {
    operator fun invoke(adminConfig: Config.AdminConfig): EmptyResult<BotError> {
        return adminTool.init(adminConfig)
            .mapError { it.toDomainError() }
    }
}