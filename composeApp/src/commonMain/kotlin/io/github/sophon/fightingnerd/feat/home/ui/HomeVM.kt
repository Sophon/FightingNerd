package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.usecase.CheckIfFirstLaunchUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.CheckCharacterHasMovesUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.SubscribeToCharacterListUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeVM(
    private val overlayService: OverlayService,
    private val checkIfFirstLaunchUseCase: CheckIfFirstLaunchUseCase,
    private val subscribeToCharacterListUseCase: SubscribeToCharacterListUseCase,
    private val checkCharacterHasMovesUseCase: CheckCharacterHasMovesUseCase,
    private val refreshUseCase: RefreshUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state
        .onStart {
            subscribeToCharacters()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeViewState(),
        )

    private var widgetDataLoadingJob: Job? = null


    init {
        firstTimeCheck()
    }


    fun refresh() {
        val current = _state.value.gameWidgetList
        if (current.isEmpty()) return

        overlayService.show(
            Toast(message = "⏳", type = Toast.Type.INFO)
        )
        widgetDataLoadingJob?.cancel()
        _state.update { state ->
            state.copy(
                gameWidgetList = current.map { it.copy(isLoading = true) }.toImmutableList()
            )
        }
        widgetDataLoadingJob = viewModelScope.launch {
            refreshUseCase.invoke().collectLatest { error ->
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

    private suspend fun subscribeToCharacters() {
        _state.value.gameWidgetList.forEach { gameWidget ->
            subscribeToCharacterListUseCase.invoke(gameWidget).collectLatest { characterList ->
                val updatedWidget = gameWidget.copy(
                    characterList = characterList.map { domainCharacter ->
                        GameWidget.Character(
                            id = domainCharacter.id,
                            displayName = domainCharacter.displayName,
                            queryName = domainCharacter.remoteQueryId,
                            iconUrl = domainCharacter.images?.iconUrl,
                        )
                    }.toImmutableList(),
                    isLoading = false,
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

                loadMoveListPerCharacter(gameWidget = updatedWidget)
            }
        }
    }

    private suspend fun loadMoveListPerCharacter(gameWidget: GameWidget) {
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
