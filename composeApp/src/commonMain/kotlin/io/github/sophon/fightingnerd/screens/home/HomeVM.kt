package io.github.sophon.fightingnerd.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.fightingnerd.screens.home.usecase.GetAvailableFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()

    init {
        loadFeatures()
    }


    fun onSavedClick() {
        //TODO:
    }

    fun onSearchClick() {
        //TODO
    }


    private fun loadFeatures() {
//        viewModelScope.launch {
//            getAvailableFeaturesUseCase.invoke().collect { result ->
//                when (result) {
//                    is Result.Success -> {
//                        _state.update { it.copy(modules = result.data) }
//                    }
//                    is Result.Error -> {
//                        Napier.e(tag = TAG) { result.error.toString() }
//                    }
//                }
//            }
//        }

        viewModelScope.launch {
            getAvailableFeaturesUseCase.invoke()
                .onSuccess { moduleList ->
                    _state.update { it.copy(modules = moduleList) }
                }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}
