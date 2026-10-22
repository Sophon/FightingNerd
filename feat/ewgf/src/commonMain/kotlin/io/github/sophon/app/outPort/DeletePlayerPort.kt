package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult

internal interface DeletePlayerPort {
    suspend fun delete(discordId: String): EmptyResult<DataError>
}
