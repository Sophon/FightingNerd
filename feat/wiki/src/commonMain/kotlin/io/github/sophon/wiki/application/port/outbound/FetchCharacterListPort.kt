package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character

internal interface FetchCharacterListPort {
    suspend fun fetchCharacterList(game: Game): Result<List<Character>, DataError.Remote>
}
