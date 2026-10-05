package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.model.AdminError
import io.github.sophon.model.UnbanRequest
import io.github.sophon.inboundPorts.UnbanUserUseCase
import io.github.sophon.app.outboundPorts.BanPort
import io.github.sophon.app.outboundPorts.AdminListPort
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess

internal class UnbanUserService(
    private val adminListPort: AdminListPort,
    private val banPort: BanPort,
): UnbanUserUseCase {
    override suspend fun invoke(unbanRequest: UnbanRequest): EmptyResult<AdminError> {
        if (adminListPort.isAdmin(unbanRequest.issuerId).not()) {
            return Result.Error(AdminError.PermissionDenied)
        }

        val result = banPort.unban(unbanRequest.offenderId)
            .mapError { error -> AdminError.Database(error) }
            .onSuccess { Napier.i(tag = TAG) { "Unbanned ${unbanRequest.offenderId}" } }
            .onError { error -> Napier.e(tag = TAG) { "${unbanRequest.offenderId}: $error" } }
        return result
    }


    private companion object {
        const val TAG = "UnbanUserService"
    }
}
