package io.github.sophon.cornerman.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.cornerman.screens.home.usecase.GetAvailableFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class HomeVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state
        .onStart {
            loadFeatures()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeViewState()
        )


    fun onSavedClick() {
        //TODO:
    }

    fun onSearchClick() {
        //TODO
    }


    private suspend fun loadFeatures() {
        getAvailableFeaturesUseCase.invoke()
            .onSuccess { featureList ->
                _state.update { it.copy(composeRegisteredFeatures = featureList) }
            }
            .onError { Napier.e(tag = TAG) { it.toString() } }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}
