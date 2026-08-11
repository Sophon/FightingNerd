package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.usecase.CheckIfFirstLaunchUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToGamesUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToCharacterListUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeVM(
    private val overlayService: OverlayService,
    private val checkIfFirstLaunchUseCase: CheckIfFirstLaunchUseCase,
    private val subscribeToGamesUseCase: SubscribeToGamesUseCase,
    private val subscribeToCharacterListUseCase: SubscribeToCharacterListUseCase,
    private val checkCharacterHasMovesUseCase: CheckCharacterHasMovesUseCase,
    private val refreshUseCase: RefreshUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = channelFlow {
        launch { subscribeToEnabledGames() }
        _state.collect { send(it) }
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
        if (_state.value.gameWidgetList.isEmpty()) return

        overlayService.show(
            Toast(message = "⏳", type = Toast.Type.INFO)
        )
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            refreshUseCase.invoke().collect { error ->
                overlayService.show(error = error)
            }
        }
    }

    fun onExpandWidget(game: Game) {
        _state.update { state ->
            val updatedList = state.gameWidgetList.map { widget ->
                if (widget.game == game) {
                    widget.copy(isExpanded = widget.isExpanded.not())
                } else {
                    widget.copy(isExpanded = false)
                }
            }
            val updatedState = state.copy(gameWidgetList = updatedList.toImmutableList())
            updatedState
        }
    }


    private fun firstTimeCheck() {
        viewModelScope.launch {
            checkIfFirstLaunchUseCase.invoke()
        }
    }

    private suspend fun subscribeToEnabledGames() {
        subscribeToGamesUseCase.invoke().collectLatest { result ->
            result
                .onSuccess { gameWikiPairList ->
                    val widgetList = gameWikiPairList.map { (game, featureInfo) ->
                        GameWidget(
                            game = game,
                            featureName = featureInfo.name,
                        )
                    }.toImmutableList()
                    _state.update { it.copy(gameWidgetList = widgetList) }
                    subscribeToCharacters(widgets = widgetList)
                }
                .onError { error ->
                    overlayService.show(error)
                }
        }
    }

    private suspend fun subscribeToCharacters(widgets: List<GameWidget>) {
        coroutineScope {
            widgets.forEach { gameWidget ->
                launch {
                    subscribeToCharacterListUseCase.invoke(gameWidget.game).collectLatest { characterList ->
                        val updatedWidget = gameWidget.copy(
                            characterList = characterList.map { domainCharacter ->
                                GameWidget.Character(
                                    id = domainCharacter.id,
                                    displayName = domainCharacter.displayName,
                                    queryName = domainCharacter.remoteQueryId,
                                    iconUrl = domainCharacter.images?.iconUrl,
                                )
                            }.toImmutableList(),
                        )

                        _state.update { state ->
                            val updatedList = state.gameWidgetList.map { widget ->
                                if (widget.game == updatedWidget.game) {
                                    updatedWidget
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(gameWidgetList = updatedList.toImmutableList())
                            updatedState
                        }

                        checkForMoveList(gameWidget = updatedWidget)
                    }
                }
            }
        }
    }

    private suspend fun checkForMoveList(gameWidget: GameWidget) {
        coroutineScope {
            gameWidget.characterList.forEach { character ->
                launch {
                    checkCharacterHasMovesUseCase.invoke(
                        game = gameWidget.game,
                        characterId = character.id,
                    ).collect { hasMoves ->
                        if (hasMoves.not()) return@collect
                        _state.update { state ->
                            val updatedList = state.gameWidgetList.map { widget ->
                                if (widget.game == gameWidget.game) {
                                    widget.withUpdatedCharacter(characterId = character.id)
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(gameWidgetList = updatedList.toImmutableList())
                            updatedState
                        }
                    }
                }
            }
        }
    }
}
