package io.github.sophon.glossaryinfil.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.model.GlossaryItem

internal interface FetchGlossaryPort {
    suspend fun fetch(): Result<List<GlossaryItem>, DataError.Remote>
}
