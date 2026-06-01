package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.Game
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadMoveListUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

internal class HomeVM(
    private val loadEmptyWidgetsUseCase: LoadEmptyWidgetsUseCase,
    private val loadGameCharacterListUseCase: LoadGameCharacterListUseCase,
    private val loadMoveListUseCase: LoadMoveListUseCase,
): ViewModel() {
    private val moveListSemaphore = Semaphore(permits = MAX_PERMITS)
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()

    init {
        loadWidgets()
        loadWidgetData()
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
        loadEmptyWidgetsUseCase.invoke()
            .onSuccess { moduleList ->
                _state.update { it.copy(gameWidgetList = moduleList) }
            }
    }

    private fun loadWidgetData() {
        _state.value.gameWidgetList.forEach { gameWidget ->
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
                        //TODO: display toast
                        Napier.e(tag = TAG) { error.toString() }

                        _state.update { state ->
                            val updatedList = state.gameWidgetList.filterNot { it.game.id == gameWidget.game.id }
                            state.copy(gameWidgetList = updatedList)
                        }
                    }
            }
        }
    }

    private suspend fun downloadMoveList(gameWidget: HomeViewState.GameWidget) {
        coroutineScope {
            gameWidget.characterList.forEach { character ->
                launch {
                    moveListSemaphore.withPermit {
                        loadMoveListUseCase.invoke(game = gameWidget.game, characterQueryId = character.queryName)
                            .onSuccess { moveList ->
                                _state.update { state ->
                                    val updatedGameWidgetList = state.gameWidgetList.map { widget ->
                                        if (widget.game == gameWidget.game) {
                                            widget.withUpdatedCharacter(
                                                characterId = character.id,
                                                moveList = moveList,
                                            )
                                        } else {
                                            widget
                                        }
                                    }
                                    state.copy(gameWidgetList = updatedGameWidgetList)
                                }

                                //TODO: then refactor to store it in the DB
                            }
                            .onError { error ->
                                //TODO: display toast
                                Napier.e(tag = TAG) { error.toString() }
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
