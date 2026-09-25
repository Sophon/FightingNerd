package io.github.sophon.wiki.adapter.outbound.ktor

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.port.outbound.FetchMoveListPort

internal class KtorMoveAdapter : FetchMoveListPort {
    override suspend fun fetchMoveList(
        game: Game,
        character: Character,
    ): Result<List<Move>, DataError.Remote> {
        TODO("Not yet implemented")
    }
}
