package io.github.sophon.fightingnerd.feat.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class MoreVM : ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    private val _navEvent = Channel<MoreItem>(Channel.BUFFERED)
    val navEvent: Flow<MoreItem> = _navEvent.receiveAsFlow()


    fun onItemClick(item: MoreItem) {
        viewModelScope.launch {
            when (item) {
                MoreItem.FeatureSettings -> _navEvent.send(item)
            }
        }
    }
}
