package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move

/**
 * Upserts the move list of an already saved character; its moves absent from the move list get a strike.
 * Atomic - the adapter owns the transaction.
 */
internal interface SaveMoveListPort {
    suspend fun saveMoveList(
        characterId: CharacterId,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local>
}
