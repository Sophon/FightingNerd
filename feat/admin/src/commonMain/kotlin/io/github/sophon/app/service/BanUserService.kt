package io.github.sophon.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.model.AdminError
import io.github.sophon.model.Ban
import io.github.sophon.model.BanRequest
import io.github.sophon.inPort.BanUserUseCase
import io.github.sophon.app.outPort.BanPort
import io.github.sophon.app.outPort.AdminListPort
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import kotlin.time.Clock

internal class BanUserService(
    private val adminListPort: AdminListPort,
    private val banPort: BanPort,
    private val clock: Clock,
): BanUserUseCase {
    override suspend fun invoke(banRequest: BanRequest): Result<Ban, AdminError> {
        if (adminListPort.isAdmin(banRequest.issuerId).not()) {
            return Result.Error(AdminError.PermissionDenied)
        }

        val now = clock.now()
        val ban = Ban(
            offenderId = banRequest.offenderId,
            bannedAt = now,
            expiresAt = (now + banRequest.duration),
            issuerId = banRequest.issuerId,
            preventBotUsage = banRequest.preventBotUsage,
        )

        val result = banPort.ban(ban)
            .map { ban }
            .mapError { error -> AdminError.Database(error) }
            .onSuccess { Napier.i(tag = TAG) { "${ban.offenderId}: $ban" } }
            .onError { error -> Napier.e(tag = TAG) { "${banRequest.offenderId}: $error" } }
        return result
    }


    private companion object {
        const val TAG = "BanUserService"
    }
}
