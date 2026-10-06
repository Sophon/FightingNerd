package io.github.sophon.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.model.StatsError

internal interface PrepareStoragePort {
    suspend fun prepare(): EmptyResult<StatsError>
}
