package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Game

internal interface DeleteCharacterListPort {
    suspend fun deleteCharacterList(game: Game): EmptyResult<DataError.Local>
}
