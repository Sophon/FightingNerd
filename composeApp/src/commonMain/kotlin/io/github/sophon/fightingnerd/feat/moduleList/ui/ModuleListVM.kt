package io.github.sophon.fightingnerd.feat.moduleList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.fightingnerd.screens.home.usecase.GetAvailableFeaturesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ModuleListVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(ModuleListState())
    val state = _state
        .onStart {
            viewModelScope.launch {
                loadFeatures()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ModuleListState()
        )


    fun onExpandModule(index: Int) {
        _state.update { it.copy(expandedModuleIndex = index) }
    }


    private suspend fun loadFeatures() {
        getAvailableFeaturesUseCase.invoke()
    }
}
