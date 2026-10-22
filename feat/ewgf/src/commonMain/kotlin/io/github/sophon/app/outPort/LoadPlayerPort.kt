package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.model.Player

internal interface LoadPlayerPort {
    suspend fun get(discordId: String): Result<Player?, DataError>
}
