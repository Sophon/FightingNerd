package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character

/**
 * Upserts [characterList] and prunes characters that have been absent from the remote for too long.
 * Atomic - the adapter owns the transaction.
 */
internal interface SaveCharacterListPort {
    suspend fun saveCharacterList(
        game: Game,
        characterList: List<Character>,
    ): EmptyResult<DataError.Local>
}
