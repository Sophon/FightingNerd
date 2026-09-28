package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move

/**
 * Upserts the character and its move list; the character's moves absent from the move list get a strike.
 * Atomic - the adapter owns the transaction.
 */
internal interface SaveCharacterMoveListPort {
    suspend fun save(
        game: Game,
        character: Character,
        moveList: List<Move>,
    ): EmptyResult<DataError.Local>
}
