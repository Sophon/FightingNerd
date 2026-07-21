package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.usecase.EnsureMoveListIsCachedUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.feat.home.usecase.CheckIfFirstLaunchUseCase
import kotlinx.collections.immutable.toImmutableList

internal class HomeVM(
    private val overlayService: OverlayService,
    private val checkIfFirstLaunchUseCase: CheckIfFirstLaunchUseCase,
    private val loadEmptyWidgetsUseCase: LoadEmptyWidgetsUseCase,
    private val loadGameCharacterListUseCase: LoadGameCharacterListUseCase,
    private val ensureMoveListIsCachedUseCase: EnsureMoveListIsCachedUseCase,
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
            val updatedState = state.copy(gameWidgetList = updatedList.toImmutableList())
            updatedState
        }
    }


    private fun loadWidgets() {
        viewModelScope.launch {
            checkIfFirstLaunchUseCase.invoke()
            loadEmptyWidgetsUseCase.invoke().collect { result ->
                result
                    .onSuccess { loadedWidgetList ->
                        val currentWidgetList = _state.value.gameWidgetList
                        val currentIds = currentWidgetList.map { it.game.id }.toSet()
                        val newIds = loadedWidgetList.map { it.game.id }.toSet()

                        val kept = currentWidgetList.filter { it.game.id in newIds }
                        val added = loadedWidgetList.filterNot { it.game.id in currentIds }
                        val merged = kept + added

                        _state.update { it.copy(gameWidgetList = merged.toImmutableList()) }

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
                            val updatedState = state.copy(gameWidgetList = updatedList.toImmutableList())
                            updatedState
                        }

                        downloadMoveList(gameWidget = loadedWidget)
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { error.toString() }
                        overlayService.show(error)

                        _state.update { state ->
                            val updatedList = state.gameWidgetList.filterNot { it.game.id == gameWidget.game.id }
                            state.copy(gameWidgetList = updatedList.toImmutableList())
                        }
                    }
            }
        }
    }

    private suspend fun downloadMoveList(gameWidget: GameWidget) {
        coroutineScope {
            val deferredList = gameWidget.characterList.map { character ->
                async {
                    moveListSemaphore.withPermit {
                        val result = ensureMoveListIsCachedUseCase.invoke(
                            game = gameWidget.game,
                            characterId = character.id,
                        )
                        if (result is Result.Error) {
                            Napier.e(tag = TAG) { result.error.toString() }
                            overlayService.show(result.error)
                        }
                        val isSuccess = result is Result.Success
                        return@async character.id to isSuccess
                    }
                }
            }

            val results = deferredList.awaitAll()
            val cachedIds = results.filter { it.second }.map { it.first }

            if (cachedIds.isEmpty()) {
                return@coroutineScope
            }

            _state.update { state ->
                val updatedGameWidgetList = state.gameWidgetList.map { widget ->
                    if (widget.game == gameWidget.game) {
                        widget.withUpdatedCharacters(characterIds = cachedIds)
                    } else {
                        widget
                    }
                }
                return@update state.copy(gameWidgetList = updatedGameWidgetList.toImmutableList())
            }
        }
    }


    companion object {
        private const val TAG = "HomeVM"
        private const val MAX_PERMITS = 2
    }
}
