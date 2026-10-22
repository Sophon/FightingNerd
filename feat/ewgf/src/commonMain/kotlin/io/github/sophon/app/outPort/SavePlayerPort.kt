package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.Player

internal interface SavePlayerPort {
    suspend fun save(player: Player): EmptyResult<DataError>
}
