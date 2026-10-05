package io.github.sophon.inboundPorts

import io.github.sophon.app.model.AdminError
import io.github.sophon.core.architecture.Result

interface IsUserAdminUseCase {
    operator fun invoke(userId: String): Result<Boolean, AdminError>
}
