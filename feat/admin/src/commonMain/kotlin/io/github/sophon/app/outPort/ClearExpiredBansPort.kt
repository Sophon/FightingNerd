package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult

internal interface ClearExpiredBansPort {
    suspend fun clear(): EmptyResult<DataError>
}
