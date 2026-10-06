package io.github.sophon.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player

interface UpdatePolarisIdUseCase {
    suspend operator fun invoke(player: Player): EmptyResult<EwgfError>
}
