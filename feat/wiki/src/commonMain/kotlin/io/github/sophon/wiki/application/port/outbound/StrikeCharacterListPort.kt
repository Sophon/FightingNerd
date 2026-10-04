package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.wiki.application.domain.model.CharacterId
import io.github.sophon.wiki.application.domain.model.wiki.Game

/**
 * The game's characters absent from [downloadedIdSet] get a strike.
 */
internal interface StrikeCharacterListPort {
    suspend fun strike(
        game: Game,
        downloadedIdSet: Set<CharacterId>,
    ): EmptyResult<DataError.Local>
}
