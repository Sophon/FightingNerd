package io.github.sophon.fightingnerd.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToAvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.wiki.inPort.ConfigureWikiUseCase
import io.github.sophon.wiki.inPort.GetAvailableGamesUseCase
import io.github.sophon.wiki.inPort.GetCharacterListUseCase
import io.github.sophon.wiki.inPort.GetMoveListUseCase
import io.github.sophon.wiki.inPort.RefreshDataUseCase
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.WikiConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import io.github.sophon.wiki.model.wiki.Game as WikiGame

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getAvailableGamesUseCase: GetAvailableGamesUseCase,
    private val getCharacterListUseCase: GetCharacterListUseCase,
    private val getMoveListUseCase: GetMoveListUseCase,
): ConfigureWikiPort, RefreshWikiPort, SubscribeToAvailableGamesPort, CharacterPort, MovePort {
    override suspend fun configure(
        availableGameSet: Set<Game>,
        enabledGameSet: Set<Game>,
    ): EmptyResult<AppError> {
        val result = WikiConfig
            .create(
                availableGameSet = availableGameSet.toWikiGameSet(),
                enabledGameSet = enabledGameSet.toWikiGameSet(),
            )
            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
            .mapError { error -> error.toDomainError() }
        return result
    }

    override fun refresh(): Flow<RefreshEvent> {
        val flow = refreshDataUseCase().map { event -> event.toDomain() }
        return flow
    }

    override fun subscribeToAvailableGames(): Flow<Set<Game>> {
        val flow = getAvailableGamesUseCase().map { wikiGameSet ->
            val gameSet = wikiGameSet
                .map { wikiGame -> wikiGame.toDomain() }
                .toSet()
            gameSet
        }
        return flow
    }

    override fun subscribeToCharacters(game: Game): Flow<List<Character>> {
        val flow = getCharacterListUseCase().map { wikiCharacterList ->
            val characterList = wikiCharacterList
                .filter { wikiCharacter -> wikiCharacter.id.game.id == game.id }
                .map { wikiCharacter -> wikiCharacter.toDomain() }
            characterList
        }
        return flow
    }

    override fun subscribeToMoves(game: Game, characterId: String): Flow<List<Move>> {
        val wikiGame = WikiGame.fromId(game.id)
        val flow = if (wikiGame == null) {
            flowOf(emptyList())
        } else {
            getMoveListUseCase(CharacterId(game = wikiGame, naturalId = characterId))
        }
        return flow
    }
}
