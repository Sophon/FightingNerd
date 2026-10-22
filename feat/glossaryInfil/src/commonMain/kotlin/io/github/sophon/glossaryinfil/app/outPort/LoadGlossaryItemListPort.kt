package io.github.sophon.glossaryinfil.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.glossaryinfil.model.GlossaryItem

/**
 * Items whose term or alt term contains [query] or [compactQuery], case-insensitive, each item once.
 */
internal interface LoadGlossaryItemListPort {
    suspend fun load(
        query: String,
        compactQuery: String,
    ): Result<List<GlossaryItem>, DataError.Local>
}
