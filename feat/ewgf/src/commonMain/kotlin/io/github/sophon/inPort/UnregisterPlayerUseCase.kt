package io.github.sophon.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.EwgfError

interface UnregisterPlayerUseCase {
    suspend operator fun invoke(discordId: String): EmptyResult<EwgfError>
}
