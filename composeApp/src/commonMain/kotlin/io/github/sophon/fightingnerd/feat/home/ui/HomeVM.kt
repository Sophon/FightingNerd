package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.fightingnerd.feat.home.usecase.LoadEmptyWidgetsUseCase
import io.github.sophon.fightingnerd.feat.home.usecase.LoadGameCharacterListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeVM(
    private val loadEmptyWidgetsUseCase: LoadEmptyWidgetsUseCase,
    private val loadGameCharacterListUseCase: LoadGameCharacterListUseCase,
): ViewModel() {
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
                    }
                    .onError {
                        //TODO: display toast
                    }
            }
        }
    }


    companion object {
        private const val TAG = "HomeVM"
    }
}
