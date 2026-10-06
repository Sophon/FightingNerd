package io.github.sophon.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.AdminError

interface ConfigureAdminToolUseCase {
    operator fun invoke(adminIdList: List<String>): EmptyResult<AdminError>
}
