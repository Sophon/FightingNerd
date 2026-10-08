package io.github.sophon.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.wiki.model.Character
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Default
import io.github.sophon.wiki.model.Filter
import io.github.sophon.wiki.model.Group
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.RefreshEvent
import io.github.sophon.wiki.model.WikiConfig
import io.github.sophon.wiki.model.WikiError
import io.github.sophon.wiki.model.wiki.Game
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/**
 * Applies the host's [WikiConfig]. Games that were enabled before and aren't in the new config get their
 * characters and moves deleted. The first call has no previous config, so nothing gets deleted.
 *
 * The config is applied even if a delete fails - an error only means some data was left behind.
 */
interface ConfigureWikiUseCase {
    suspend operator fun invoke(wikiConfig: WikiConfig): EmptyResult<WikiError>
}

interface GetAvailableGamesUseCase {
    operator fun invoke(): Flow<Set<Game>>
}

interface GetCharacterListUseCase {
    operator fun invoke(): Flow<List<Character>>
}

interface GetCharacterUseCase {
    suspend operator fun invoke(characterId: CharacterId): Result<Character, WikiError>
}

interface GetFiltersUseCase {
    operator fun invoke(game: Game): Set<Filter>
}

interface GetGroupsUseCase {
    operator fun invoke(
        game: Game,
        extras: List<String> = emptyList(),
    ): List<Group> = listOf(Default)
}

interface GetMoveListUseCase {
    operator fun invoke(characterId: CharacterId): Flow<List<Move>>
}

interface GetMoveUseCase {
    suspend operator fun invoke(characterId: CharacterId, input: String): Result<Move, WikiError>
}

interface GetUpdateTimeStampUseCase {
    operator fun invoke(game: Game): Flow<Instant?>
}

/**
 * Stored inputs are normalized per game on refresh - a user's `1,1,3` has to become `113` before it can match.
 */
interface NormalizeMoveInputUseCase {
    operator fun invoke(game: Game, input: String): String
}

/**
 * Downloads and saves the characters and moves of every enabled game. Emits [RefreshEvent.Failed] for every
 * character list or move list that failed, then a [RefreshEvent.Finished] per game.
 * Games that download move lists per character also emit [RefreshEvent.Progress] after each character,
 * whether its move list succeeded or not.
 * Waits for the config if the wiki isn't configured yet.
 *
 * The flow is cold - the refresh runs while it's collected and stops when the collection is cancelled.
 * Overlapping collections run one after another - two refreshes never write at the same time.
 */
interface RefreshDataUseCase {
    operator fun invoke(): Flow<RefreshEvent>

    /**
     * Same as the parameterless refresh, limited to the games of [gameSet] that are enabled.
     */
    operator fun invoke(gameSet: Set<Game>): Flow<RefreshEvent>
}
