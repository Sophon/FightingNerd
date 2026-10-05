package io.github.sophon.inPort

import io.github.sophon.model.AdminError
import io.github.sophon.model.UnbanRequest
import io.github.sophon.core.architecture.EmptyResult

interface UnbanUserUseCase {
    suspend operator fun invoke(unbanRequest: UnbanRequest): EmptyResult<AdminError>
}
