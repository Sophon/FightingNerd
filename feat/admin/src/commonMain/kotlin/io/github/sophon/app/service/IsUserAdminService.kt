package io.github.sophon.app.service

import io.github.sophon.app.model.AdminError
import io.github.sophon.inboundPorts.IsUserAdminUseCase
import io.github.sophon.app.outboundPorts.AdminListPort
import io.github.sophon.core.architecture.Result

internal class IsUserAdminService(
    private val adminListPort: AdminListPort,
): IsUserAdminUseCase {
    override fun invoke(userId: String): Result<Boolean, AdminError> {
        val result = Result.Success(adminListPort.isAdmin(userId))
        return result
    }
}
