package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.wiki.Game

/**
 * The game's characters absent from [downloadedIdSet] get a strike.
 */
internal interface StrikeCharacterListPort {
    suspend fun strike(
        game: Game,
        downloadedIdSet: Set<CharacterId>,
    ): EmptyResult<DataError.Local>
}
