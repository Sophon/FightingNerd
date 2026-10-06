package io.github.sophon.inPort

import io.github.sophon.core.architecture.Result
import io.github.sophon.model.AdminError

interface IsUserAdminUseCase {
    operator fun invoke(userId: String): Result<Boolean, AdminError>
}
