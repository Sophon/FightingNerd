package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.usecase.EnsureMoveListIsCached
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

internal class HomeVM(
    private val overlayService: OverlayService,
    private val loadEmptyWidgetsUseCase: LoadEmptyWidgetsUseCase,
    private val loadGameCharacterListUseCase: LoadGameCharacterListUseCase,
    private val ensureMoveListIsCached: EnsureMoveListIsCached,
): ViewModel() {
    private val moveListSemaphore = Semaphore(permits = MAX_PERMITS)
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()

    init {
        loadWidgets()
    }


    fun onSavedClick() {
        //TODO:
    }

    fun onSearchClick() {
        //TODO
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
            val updatedState = state.copy(gameWidgetList = updatedList)
            updatedState
        }
    }


    private fun loadWidgets() {
        viewModelScope.launch {
            loadEmptyWidgetsUseCase.invoke().collect { result ->
                result
                    .onSuccess { loadedWidgetList ->
                        val currentWidgetList = _state.value.gameWidgetList
                        val currentIds = currentWidgetList.map { it.game.id }.toSet()
                        val newIds = loadedWidgetList.map { it.game.id }.toSet()

                        val kept = currentWidgetList.filter { it.game.id in newIds }
                        val added = loadedWidgetList.filterNot { it.game.id in currentIds }
                        val merged = kept + added

                        _state.update { it.copy(gameWidgetList = merged) }

                        loadWidgetData(added)
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { "loadWidgets(): $error" }
                        overlayService.show(error)
                    }
            }
        }
    }

    private fun loadWidgetData(widgetList: List<GameWidget>) {
        widgetList.forEach { gameWidget ->
            viewModelScope.launch {
                loadGameCharacterListUseCase.invoke(gameWidget)
                    .onSuccess { loadedWidget ->
                        _state.update { state ->
                            val updatedList = state.gameWidgetList.map { widget ->
                                if (widget.game == loadedWidget.game) {
                                    loadedWidget
                                } else {
                                    widget
                                }
                            }
                            val updatedState = state.copy(gameWidgetList = updatedList)
                            updatedState
                        }

                        downloadMoveList(gameWidget = loadedWidget)
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { error.toString() }
                        overlayService.show(error)

                        _state.update { state ->
                            val updatedList = state.gameWidgetList.filterNot { it.game.id == gameWidget.game.id }
                            state.copy(gameWidgetList = updatedList)
                        }
                    }
            }
        }
    }

    private suspend fun downloadMoveList(gameWidget: GameWidget) {
        coroutineScope {
            gameWidget.characterList.forEach { character ->
                launch {
                    moveListSemaphore.withPermit {
                        ensureMoveListIsCached.invoke(game = gameWidget.game, characterId = character.id)
                            .onSuccess {
                                _state.update { state ->
                                    val updatedGameWidgetList = state.gameWidgetList.map { widget ->
                                        if (widget.game == gameWidget.game) {
                                            widget.withUpdatedCharacter(characterId = character.id)
                                        } else {
                                            widget
                                        }
                                    }
                                    state.copy(gameWidgetList = updatedGameWidgetList)
                                }
                            }
                            .onError { error ->
                                Napier.e(tag = TAG) { error.toString() }
                                overlayService.show(error)
                            }
                    }
                }
            }
        }
    }


    companion object {
        private const val TAG = "HomeVM"
        private const val MAX_PERMITS = 6
    }
}
