package io.github.sophon.fightingnerd.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.Character
import io.github.sophon.fightingnerd.app.model.ComposeConfig
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Move
import io.github.sophon.fightingnerd.app.model.MoveFilter
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.model.game.T8Properties
import io.github.sophon.fightingnerd.app.outPort.AvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.CharacterPort
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.MoveFilterPort
import io.github.sophon.fightingnerd.app.outPort.MoveGroupPort
import io.github.sophon.fightingnerd.app.outPort.MovePort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.wiki.inPort.ConfigureWikiUseCase
import io.github.sophon.wiki.inPort.GetAvailableGamesUseCase
import io.github.sophon.wiki.inPort.GetCharacterListUseCase
import io.github.sophon.wiki.inPort.GetFiltersUseCase
import io.github.sophon.wiki.inPort.GetGroupsUseCase
import io.github.sophon.wiki.inPort.GetMoveListUseCase
import io.github.sophon.wiki.inPort.RefreshDataUseCase
import io.github.sophon.wiki.model.CharacterId
import io.github.sophon.wiki.model.Default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import io.github.sophon.wiki.model.Move as WikiMove
import io.github.sophon.wiki.model.game.T8Properties as WikiT8Properties
import io.github.sophon.wiki.model.wiki.Game as WikiGame

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
    private val getAvailableGamesUseCase: GetAvailableGamesUseCase,
    private val getCharacterListUseCase: GetCharacterListUseCase,
    private val getMoveListUseCase: GetMoveListUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
): ConfigureWikiPort, RefreshWikiPort, AvailableGamesPort, CharacterPort, MovePort, MoveFilterPort, MoveGroupPort {
    override suspend fun configure(
        composeConfig: ComposeConfig,
        enabledGameIdSet: Set<String>,
    ): EmptyResult<AppError> {
        val result = composeConfig.toWikiConfig(enabledGameIdSet)
            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
            .mapError { error -> error.toDomainError() }
        return result
    }

    override fun refresh(): Flow<RefreshEvent> {
        val flow = refreshDataUseCase().map { event -> event.toDomain() }
        return flow
    }

    override fun subscribe(): Flow<Set<Game>> {
        val flow = getAvailableGamesUseCase().map { wikiGameSet ->
            val gameSet = wikiGameSet
                .map { wikiGame -> wikiGame.toDomain() }
                .toSet()
            gameSet
        }
        return flow
    }

    override fun subscribeToCharacters(gameId: String): Flow<List<Character>> {
        val flow = getCharacterListUseCase().map { wikiCharacterList ->
            val characterList = wikiCharacterList
                .filter { wikiCharacter -> wikiCharacter.id.game.id == gameId }
                .map { wikiCharacter -> wikiCharacter.toDomain() }
            characterList
        }
        return flow
    }

    override fun subscribeToMoves(gameId: String, characterId: String): Flow<List<Move>> {
        val wikiGame = WikiGame.fromId(gameId)
        val flow = if (wikiGame == null) {
            flowOf(emptyList())
        } else {
            getMoveListUseCase(CharacterId(game = wikiGame, naturalId = characterId))
                .map { wikiMoveList -> wikiMoveList.toDomain(wikiGame) }
        }
        return flow
    }

    override fun loadFilters(gameId: String): Result<Set<MoveFilter.Named>, AppError> {
        val wikiGame = WikiGame.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))

        val filterSet = getFiltersUseCase(wikiGame)
            .map { filter -> MoveFilter.Named(filter.name) }
            .toSet()
        return Result.Success(filterSet)
    }

    override fun loadGroupIdList(gameId: String, moveList: List<Move>): Result<List<String>, AppError> {
        val wikiGame = WikiGame.fromId(gameId) ?: return Result.Error(AppError.GameNotFound(gameId))

        val stanceList = moveList
            .mapNotNull { move -> (move.gameProperties as? T8Properties)?.stance }
            .toStanceList()
        val groupIdList = (getGroupsUseCase(wikiGame, stanceList).map { group -> group.id } + Default.id)
        return Result.Success(groupIdList)
    }


    private fun List<WikiMove>.toDomain(wikiGame: WikiGame): List<Move> {
        val stanceList = this
            .mapNotNull { wikiMove -> (wikiMove.gameProperties as? WikiT8Properties)?.stance }
            .toStanceList()
        val groupList = getGroupsUseCase(wikiGame, stanceList)
        val filterSet = getFiltersUseCase(wikiGame)

        val moveList = map { wikiMove ->
            val group = groupList.firstOrNull { group -> group.predicate(wikiMove) } ?: Default
            val filterNameSet = filterSet
                .filter { filter -> filter.predicate(wikiMove) }
                .map { filter -> filter.name }
                .toSet()
            val move = wikiMove.toDomain(groupId = group.id, filterNameSet = filterNameSet)
            move
        }
        return moveList
    }

    private fun List<String>.toStanceList(): List<String> {
        val stanceList = this
            .filter { stance -> stance.isNotBlank() }
            .map { stance -> stance.uppercase() }
            .distinct()
        return stanceList
    }
}
