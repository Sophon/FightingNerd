package io.github.sophon.fightingnerd.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.fightingnerd.screens.home.usecase.GetAvailableFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state
        .onStart {
            viewModelScope.launch {
                loadFeatures()
            }
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
        getAvailableFeaturesUseCase.invoke().collect { result ->
            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(wikiModules = result.data) }
                }
                is Result.Error -> {
                    Napier.e(tag = TAG) { result.error.toString() }
                }
            }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}
