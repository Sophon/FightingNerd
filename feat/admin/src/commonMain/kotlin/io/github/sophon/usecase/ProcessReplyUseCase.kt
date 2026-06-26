package io.github.sophon.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.integration.model.AdminError
import io.github.sophon.integration.model.AdminResult
import io.github.sophon.integration.model.Source

internal class ProcessReplyUseCase {
    fun invoke(
        origin: Source,
        target: Source,
        reply: String,
        adminConfig: Config.AdminConfig
    ): Result<AdminResult, AdminError> {
        val authorId = origin.id

        if (adminConfig.administratorIdList.contains(authorId).not()) {
            return Result.Error(AdminError.PermissionDenied())
        }

        val result = AdminResult(
            source = target,
            message = reply,
        )
        return Result.Success(result)
    }
}