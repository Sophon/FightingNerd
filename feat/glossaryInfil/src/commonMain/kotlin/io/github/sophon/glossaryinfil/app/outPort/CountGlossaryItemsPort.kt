package io.github.sophon.glossaryinfil.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result

internal interface CountGlossaryItemsPort {
    suspend fun count(): Result<Long, DataError.Local>
}
