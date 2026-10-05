package io.github.sophon.app.port.inbound

import io.github.sophon.app.domain.model.AdminError
import io.github.sophon.app.domain.model.ModerationRequest
import io.github.sophon.core.architecture.EmptyResult

interface UnbanUserUseCase {
    suspend operator fun invoke(moderationRequest: ModerationRequest): EmptyResult<AdminError>
}
