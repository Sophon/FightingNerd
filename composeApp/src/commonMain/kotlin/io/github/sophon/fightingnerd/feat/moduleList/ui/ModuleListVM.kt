package io.github.sophon.fightingnerd.feat.moduleList.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class ModuleListVM: ViewModel() {
    private val _state = MutableStateFlow(ModuleListState())
    val state = _state.asStateFlow()

    init {
        //TODO: load and initialize modules
    }


    fun onExpandModule(index: Int) {
        _state.update { it.copy(expandedModuleIndex = index) }
    }
}
