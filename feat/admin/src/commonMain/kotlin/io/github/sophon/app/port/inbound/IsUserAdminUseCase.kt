package io.github.sophon.app.port.inbound

import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.core.architecture.Result

interface IsUserAdminUseCase {
    operator fun invoke(userId: String): Result<Boolean, AdminError>
}
