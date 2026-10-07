package io.github.sophon.fightingnerd.adapter.inbound.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.home_refresh_refreshing
import fightingnerd.composeapp.generated.resources.home_refresh_success
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.inPort.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.inPort.FirstTimeConfigUseCase
import io.github.sophon.fightingnerd.inPort.RefreshDataUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToCharactersUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToGamesUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class HomeVM(
    private val overlayService: OverlayService,
    private val firstTimeConfigUseCase: FirstTimeConfigUseCase,
    private val subscribeToGamesUseCase: SubscribeToGamesUseCase,
    private val subscribeToCharactersUseCase: SubscribeToCharactersUseCase,
    private val checkCharacterHasMovesUseCase: CheckCharacterHasMovesUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = flow {
        coroutineScope {
            launch { subscribeToEnabledGames() }
            emitAll(_state)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeViewState(),
    )

    private var refreshJob: Job? = null


    init {
        firstTimeCheck()
    }


    fun refresh() {
        if (_state.value.gameFeatureList.isEmpty()) return

        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            overlayService.show(
                Toast(
                    message = getString(Res.string.home_refresh_refreshing),
                    type = Toast.Type.INFO,
                )
            )
            refreshDataUseCase().collect { outcome ->
                when (outcome) {
                    is RefreshEvent.Finished -> {
                        overlayService.show(
                            Toast(
                                message = getString(
                                    Res.string.home_refresh_success,
                                    "TODO: game refresh",
                                    outcome.successCount,
                                ),
                                type = Toast.Type.SUCCESS,
                            )
                        )
                    }
                    is RefreshEvent.Failed -> {
                        overlayService.show(error = outcome.error)
                    }
                }
            }
        }
    }

    fun onExpandWidget(game: Game) {
        _state.update { state ->
            val updatedList = state.gameFeatureList.map { widget ->
                when {
                    (widget.game == game) -> {
                        widget.copy(isExpanded = widget.isExpanded.not())
                    }
                    widget.isExpanded -> {
                        widget.copy(isExpanded = false)
                    } else -> {
                        widget
                    }
                }
            }
            val updatedState = state.copy(gameFeatureList = updatedList.toImmutableList())
            updatedState
        }
    }


    private fun firstTimeCheck() {
        viewModelScope.launch {
            firstTimeConfigUseCase()
        }
    }

    private suspend fun subscribeToEnabledGames() {
        subscribeToGamesUseCase().collectLatest { result ->
            result
                .onSuccess { gameList ->
                    val existingByGame = _state.value.gameFeatureList.associateBy { it.game }
                    val widgetList = gameList.map { game ->
                        val existing = existingByGame[game]
                        val widget = GameFeature(
                            game = game,
                            featureName = game.wikiName,
                            characterList = existing?.characterList ?: persistentListOf(),
                            isExpanded = existing?.isExpanded ?: false,
                        )
                        widget
                    }.toImmutableList()
                    _state.update { it.copy(gameFeatureList = widgetList) }
                    subscribeToCharacters(widgets = widgetList)
                }
                .onError { error ->
                    overlayService.show(error)
                }
        }
    }

    private suspend fun subscribeToCharacters(widgets: List<GameFeature>) {
        coroutineScope {
            widgets.forEach { gameWidget ->
                launch {
                    subscribeToCharactersUseCase(gameWidget.game).collectLatest { characterList ->
                        val newState = _state.updateAndGet { state ->
                            val updatedList = state.gameFeatureList.map { widget ->
                                if (widget.game == gameWidget.game) {
                                    val existingCharsById = widget.characterList.associateBy { it.id }
                                    val newCharList = characterList.map { domainCharacter ->
                                        val existing = existingCharsById[domainCharacter.id]
                                        val ui = GameFeature.UiCharacter(
                                            id = domainCharacter.id,
                                            displayName = domainCharacter.displayName,
                                            iconUrl = domainCharacter.iconUrl,
                                            hasMoves = existing?.hasMoves ?: false,
                                        )
                                        ui
                                    }.toImmutableList()
                                    val merged = widget.copy(characterList = newCharList)
                                    merged
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(gameFeatureList = updatedList.toImmutableList())
                            updatedState
                        }

                        val updatedWidget = newState.gameFeatureList.first { it.game == gameWidget.game }
                        checkForMoveList(gameWidget = updatedWidget)
                    }
                }
            }
        }
    }

    private suspend fun checkForMoveList(gameWidget: GameFeature) {
        coroutineScope {
            gameWidget.characterList.forEach { character ->
                launch {
                    checkCharacterHasMovesUseCase.invoke(
                        game = gameWidget.game,
                        characterId = character.id,
                    ).collect { hasMoves ->
                        if (hasMoves.not()) return@collect
                        _state.update { state ->
                            val updatedList = state.gameFeatureList.map { widget ->
                                if (widget.game == gameWidget.game) {
                                    widget.withUpdatedCharacter(characterId = character.id)
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(gameFeatureList = updatedList.toImmutableList())
                            updatedState
                        }
                    }
                }
            }
        }
    }
}
