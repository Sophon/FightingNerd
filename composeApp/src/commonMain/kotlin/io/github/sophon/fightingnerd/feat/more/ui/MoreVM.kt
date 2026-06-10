package io.github.sophon.fightingnerd.feat.more.ui

import androidx.lifecycle.ViewModel
import io.github.sophon.fightingnerd.feat.more.model.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class MoreVM(
    //
): ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    fun onThemeItemClick(isDialogVisible: Boolean) {
        _state.update { it.copy(isThemeSelectorVisible = isDialogVisible) }
    }

    fun onChangeTheme(theme: Theme) {
        //TODO: call usecase
    }
}
