package io.github.sophon.glossaryinfil.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.glossaryinfil.model.GlossaryItem

/**
 * Replaces the stored glossary with [itemList]. Atomic - the adapter owns the transaction.
 */
internal interface ReplaceGlossaryPort {
    suspend fun replace(itemList: List<GlossaryItem>): EmptyResult<DataError.Local>
}
