package io.github.sophon.inboundPorts

import io.github.sophon.app.model.AdminError
import io.github.sophon.app.model.ModerationRequest
import io.github.sophon.core.architecture.EmptyResult

interface UnbanUserUseCase {
    suspend operator fun invoke(moderationRequest: ModerationRequest): EmptyResult<AdminError>
}
