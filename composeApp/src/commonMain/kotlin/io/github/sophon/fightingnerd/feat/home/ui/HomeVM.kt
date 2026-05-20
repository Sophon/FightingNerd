package io.github.sophon.fightingnerd.feat.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.fightingnerd.feat.home.usecase.LoadModulesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HomeVM(
    private val loadModulesUseCase: LoadModulesUseCase,
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
        viewModelScope.launch {
            loadModulesUseCase.invoke()
                .onSuccess { moduleList ->
                    _state.update { it.copy(modules = moduleList) }
                }
        }
    }

    companion object {
        private const val TAG = "HomeVM"
    }
}
