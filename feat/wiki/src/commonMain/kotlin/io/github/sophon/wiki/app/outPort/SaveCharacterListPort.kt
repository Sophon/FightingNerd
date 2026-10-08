package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.Character

/**
 * Upserts the characters without touching their moves.
 * Atomic - the adapter owns the transaction, so the whole list lands at once.
 */
internal interface SaveCharacterListPort {
    suspend fun saveCharacterList(
        characterList: List<Character>,
    ): EmptyResult<DataError.Local>
}
