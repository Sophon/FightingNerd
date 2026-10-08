package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move

/**
 * Upserts every character with its move list; each character's moves absent from its move list get a strike.
 * Atomic - the adapter owns the transaction, so the whole game lands at once.
 */
internal interface SaveGameDataPort {
    suspend fun saveGameData(
        gameData: List<Pair<Character, List<Move>>>,
    ): EmptyResult<DataError.Local>
}
