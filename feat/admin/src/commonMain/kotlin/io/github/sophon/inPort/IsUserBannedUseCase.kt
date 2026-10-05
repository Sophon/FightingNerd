package io.github.sophon.inPort

import io.github.sophon.model.AdminError
import io.github.sophon.core.architecture.Result

interface IsUserBannedUseCase {
    suspend operator fun invoke(userId: String): Result<Boolean, AdminError>
}
