package io.github.sophon.usecase

import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.data.BanRepo
import io.github.sophon.domain.AdminError
import io.github.sophon.domain.AdminResult
import io.github.sophon.domain.Source

internal class ProcessFeedbackUseCase(
    private val repo: BanRepo,
) {
    suspend fun invoke(
        origin: Source,
        feedback: String,
        adminConfig: Config.AdminConfig
    ): Result<AdminResult, AdminError> {
        val authorId = origin.id

        val ban = when (val result = repo.getBanStatus(offenderId = authorId)) {
            is Result.Success -> result.data
            is Result.Error -> null
        }

        if (ban != null && adminConfig.administratorIdList.contains(authorId).not()) {
            return Result.Error(AdminError.UserBanned("$authorId: $ban"))
        }

        val result = AdminResult(
            source = origin,
            message = feedback,
        )
        return Result.Success(result)
    }
}
