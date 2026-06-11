package io.github.sophon.fightingnerd.feat.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.feat.more.model.Theme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MoreVM(
    //
): ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    private val _navEvent = Channel<MoreItem>(Channel.BUFFERED)
    val navEvent: Flow<MoreItem> = _navEvent.receiveAsFlow()

    fun onThemeItemClick(isDialogVisible: Boolean) {
        _state.update { it.copy(isThemeSelectorVisible = isDialogVisible) }
    }

    fun onItemClick(item: MoreItem) {
        viewModelScope.launch {
            when (item) {
                MoreItem.Theme -> _state.update { it.copy(isThemeSelectorVisible = true) }
                MoreItem.Donate -> { /*TODO*/ }
                MoreItem.FeatureSettings -> _navEvent.send(item)
            }
        }
    }

    fun onThemeSelect(theme: Theme) {
        onThemeItemClick(isDialogVisible = false)
        //TODO: call usecase
    }
}
