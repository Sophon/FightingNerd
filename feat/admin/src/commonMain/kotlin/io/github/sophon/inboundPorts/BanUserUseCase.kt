package io.github.sophon.inboundPorts

import io.github.sophon.app.model.AdminError
import io.github.sophon.app.model.Ban
import io.github.sophon.app.model.ModerationRequest
import io.github.sophon.core.architecture.Result

interface BanUserUseCase {
    //upsert
    suspend operator fun invoke(moderationRequest: ModerationRequest): Result<Ban, AdminError>
}
