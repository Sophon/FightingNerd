package io.github.sophon.app.domain.service

import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.app.port.inbound.IsUserAdminUseCase
import io.github.sophon.app.port.outbound.AdminListPort
import io.github.sophon.core.architecture.Result

internal class IsUserAdminService(
    private val adminListPort: AdminListPort,
): IsUserAdminUseCase {
    override fun invoke(userId: String): Result<Boolean, AdminError> {
        val isAdmin = adminListPort.load().contains(userId)
        val result = Result.Success(isAdmin)
        return result
    }
}
