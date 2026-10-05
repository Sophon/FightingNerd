package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.app.model.AdminError
import io.github.sophon.app.model.Ban
import io.github.sophon.app.model.ModerationRequest
import io.github.sophon.inboundPorts.BanUserUseCase
import io.github.sophon.app.outboundPorts.BanPort
import io.github.sophon.app.outboundPorts.AdminListPort
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess

internal class BanUserService(
    private val adminListPort: AdminListPort,
    private val banPort: BanPort,
): BanUserUseCase {
    override suspend fun invoke(moderationRequest: ModerationRequest): Result<Ban, AdminError> {
        val isAdmin = adminListPort.load().contains(moderationRequest.authorId)
        if (!isAdmin) {
            return Result.Error(AdminError.PermissionDenied())
        }

        val result = banPort.ban(moderationRequest)
            .mapError { error -> AdminError.DatabaseError(error.toString()) }
            .onSuccess { ban -> Napier.i(tag = TAG) { "${ban.offenderId}: $ban" } }
            .onError { error -> Napier.e(tag = TAG) { "${moderationRequest.offenderId}: $error" } }
        return result
    }


    private companion object {
        const val TAG = "BanUserService"
    }
}
