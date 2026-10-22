package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult

internal interface UpdatePolarisIdPort {
    suspend fun update(discordId: String, polarisId: String): EmptyResult<DataError>
}
