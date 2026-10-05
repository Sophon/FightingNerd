package io.github.sophon.inPort

import io.github.sophon.model.AdminError
import io.github.sophon.core.architecture.EmptyResult

interface ConfigureAdminToolUseCase {
    operator fun invoke(adminIdList: List<String>): EmptyResult<AdminError>
}
