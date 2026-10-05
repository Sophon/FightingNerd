package io.github.sophon.app.port.inbound

import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.app.domain.model.Ban
import io.github.sophon.app.domain.model.ModerationRequest
import io.github.sophon.core.architecture.Result

interface BanUserUseCase {
    //upsert
    suspend operator fun invoke(moderationRequest: ModerationRequest): Result<Ban, AdminError>
}
