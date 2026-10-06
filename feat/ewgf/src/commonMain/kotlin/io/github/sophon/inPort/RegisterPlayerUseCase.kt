package io.github.sophon.inPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.EwgfError
import io.github.sophon.model.Player

interface RegisterPlayerUseCase {
    //upsert
    suspend operator fun invoke(player: Player): EmptyResult<EwgfError>
}
