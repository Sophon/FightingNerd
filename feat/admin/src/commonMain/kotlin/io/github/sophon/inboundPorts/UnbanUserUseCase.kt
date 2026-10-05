package io.github.sophon.inboundPorts

import io.github.sophon.app.model.AdminError
import io.github.sophon.app.model.UnbanRequest
import io.github.sophon.core.architecture.EmptyResult

interface UnbanUserUseCase {
    suspend operator fun invoke(unbanRequest: UnbanRequest): EmptyResult<AdminError>
}
