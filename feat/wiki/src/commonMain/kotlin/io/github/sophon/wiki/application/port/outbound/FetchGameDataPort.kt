package io.github.sophon.wiki.application.port.outbound

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.wiki.application.domain.model.Character
import io.github.sophon.wiki.application.domain.model.Move
import kotlinx.coroutines.flow.Flow

/**
 * Emits each character of [Game] paired with its move list - how many requests that takes is up to the adapter.
 * A failure that affects the whole game (e.g. the character list) is a single [Result.Error] emission.
 */
internal interface FetchGameDataPort {
    fun fetch(game: Game): Flow<Result<Pair<Character, List<Move>>, DataError.Remote>>
}
