package io.github.sophon.app.domain.service

import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.app.domain.model.Ban
import io.github.sophon.app.domain.model.ModerationRequest
import io.github.sophon.app.port.inbound.BanUserUseCase
import io.github.sophon.app.port.outbound.BanPort
import io.github.sophon.core.architecture.Result

internal class BanUserService(
    private val banPort: BanPort,
): BanUserUseCase {
    override suspend fun invoke(moderationRequest: ModerationRequest): Result<Ban, AdminError> {
        TODO("Not yet implemented")
    }
}
