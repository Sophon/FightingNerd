package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.home.ui.HomeViewState.GameWidget
import io.github.sophon.fightingnerd.feat.home.usecase.CheckIfFirstLaunchUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadMoveListUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

internal class HomeVM(
    private val overlayService: OverlayService,
    private val checkIfFirstLaunchUseCase: CheckIfFirstLaunchUseCase,
    private val loadEmptyWidgetsUseCase: LoadEmptyWidgetsUseCase,
    private val loadGameCharacterListUseCase: LoadGameCharacterListUseCase,
    private val loadMoveListUseCase: LoadMoveListUseCase,
): ViewModel() {
    private val moveListSemaphore = Semaphore(permits = MAX_PERMITS)
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()

    private var widgetDataLoadingJob: Job? = null

    init {
        loadWidgets()
    }


    fun refresh() {
        val current = _state.value.gameWidgetList
        if (current.isEmpty()) return

        overlayService.show(
            Toast(message = "⏳", type = Toast.Type.INFO)
        )
        widgetDataLoadingJob?.cancel()
        _state.update { state ->
            state.copy(gameWidgetList = current.map { it.copy(isLoading = true) }.toImmutableList())
        }
        widgetDataLoadingJob = viewModelScope.launch {
            loadWidgetData(_state.value.gameWidgetList, forceDownload = true)
        }
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

                        widgetDataLoadingJob?.cancel()
                        widgetDataLoadingJob = viewModelScope.launch { loadWidgetData(added) }
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { "loadWidgets(): $error" }
                        overlayService.show(error)
                    }
            }
        }
    }

    private suspend fun loadWidgetData(
        widgetList: List<GameWidget>,
        forceDownload: Boolean = false,
    ) {
        coroutineScope {
            widgetList.forEach { gameWidget ->
                launch {
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

                            val allSucceeded = loadMoveList(gameWidget = loadedWidget, forceDownload = forceDownload)
                            if (forceDownload && allSucceeded) {
                                overlayService.show(
                                    Toast(message = loadedWidget.game.displayName, type = Toast.Type.SUCCESS)
                                )
                            }
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
    }

    private suspend fun loadMoveList(
        gameWidget: GameWidget,
        forceDownload: Boolean = false,
    ): Boolean {
        return coroutineScope {
            val deferredList = gameWidget.characterList.map { character ->
                async {
                    moveListSemaphore.withPermit {
                        val result = loadMoveListUseCase.invoke(
                            game = gameWidget.game,
                            characterId = character.id,
                            forceDownload = forceDownload,
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
            val allSucceeded = results.all { it.second }
            val cachedIds = results.filter { it.second }.map { it.first }

            if (cachedIds.isNotEmpty()) {
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

            return@coroutineScope allSucceeded
        }
    }


    companion object {
        private const val TAG = "HomeVM"
        private const val MAX_PERMITS = 2
    }
}
