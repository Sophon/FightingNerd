package io.github.sophon.fightingnerd.feat.bottomBar.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class BottomBarVM: ViewModel() {
    private val _state = MutableStateFlow(BottomBarState())
    val state = _state.asStateFlow()


    fun onItemClick(index: Int) {
        _state.update { it.copy(selectedItemIndex = index) }
    }
}