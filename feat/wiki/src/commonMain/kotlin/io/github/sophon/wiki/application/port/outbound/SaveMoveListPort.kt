package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

/**
 * Upserts [moveList] for [character] and prunes moves that have been absent from the remote for too long.
 * Atomic - the adapter owns the transaction.
 */
internal interface SaveMoveListPort {
    suspend fun saveMoveList(
        game: Game,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local>
}
