package io.github.sophon.app.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult

internal interface ClearExpiredBansPort {
    suspend fun clear(): EmptyResult<DataError>
}
