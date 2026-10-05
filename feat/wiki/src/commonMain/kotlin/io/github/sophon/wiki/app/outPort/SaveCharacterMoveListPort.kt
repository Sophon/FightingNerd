package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move

/**
 * Upserts the character and its move list; the character's moves absent from the move list get a strike.
 * Atomic - the adapter owns the transaction.
 */
internal interface SaveCharacterMoveListPort {
    suspend fun save(
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local>
}
