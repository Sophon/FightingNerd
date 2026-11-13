package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game

internal interface FetchGameDataPort {
    suspend fun fetchCharacterList(game: Game): Result<List<Character>, DataError.Remote>

    suspend fun fetchMoveList(character: Character): Result<List<Move>, DataError.Remote>
}
