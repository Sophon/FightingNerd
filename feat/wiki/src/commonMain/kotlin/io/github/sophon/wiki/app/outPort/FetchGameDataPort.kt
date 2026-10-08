package io.github.sophon.wiki.app.outPort

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.wiki.Game

/**
 * [Game.separateCharMoveDownload] decides which shape a game is fetched in -
 * separate: [fetchCharacterList], then [fetchMoveList] per character; bulk: [fetchGameData].
 * A shape the game's wiki doesn't serve is [DataError.Remote.PAGE_NOT_FOUND].
 */
internal interface FetchGameDataPort {
    suspend fun fetchCharacterList(game: Game): Result<List<Character>, DataError.Remote>

    suspend fun fetchMoveList(character: Character): Result<List<Move>, DataError.Remote>

    suspend fun fetchGameData(game: Game): Result<List<Pair<Character, List<Move>>>, DataError.Remote>
}
