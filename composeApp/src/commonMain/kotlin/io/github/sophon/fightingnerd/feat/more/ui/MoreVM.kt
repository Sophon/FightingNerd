package io.github.sophon.fightingnerd.feat.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.fightingnerd.core.usecase.OpenUrlUseCase
import io.github.sophon.fightingnerd.feat.more.model.DonationMethod
import io.github.sophon.fightingnerd.feat.more.model.MoreItem
import io.github.sophon.fightingnerd.feat.more.usecase.SetThemeUseCase
import io.github.sophon.fightingnerd.theme.ThemeMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MoreVM(
    private val openUrlUseCase: OpenUrlUseCase,
    private val setThemeUseCase: SetThemeUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state.asStateFlow()

    private val _navEvent = Channel<MoreItem>(Channel.BUFFERED)
    val navEvent: Flow<MoreItem> = _navEvent.receiveAsFlow()

    fun onThemeDialog(isDialogVisible: Boolean) {
        _state.update {
            val themeSelectorDialog = if (isDialogVisible) MoreState.ThemeSelectorDialog() else null
            it.copy(themeSelectorDialog = themeSelectorDialog)
        }
    }

    fun onThemeSelect(themeMode: ThemeMode) {
        onThemeDialog(isDialogVisible = false)

        viewModelScope.launch {
            setThemeUseCase.invoke(themeMode)
                .onError { Napier.e(tag = TAG) { "onThemeSelect: $it" } }
        }
    }

    fun onItemClick(item: MoreItem) {
        viewModelScope.launch {
            when (item) {
                MoreItem.Theme -> onThemeDialog(isDialogVisible = true)
                MoreItem.FeatureSettings -> _navEvent.send(item)
            }
        }
    }

    fun onDonateClick(isVisible: Boolean) {
        _state.update {
            val dialog = if (isVisible) MoreState.DonationDialog() else null
            it.copy(donationSelectorDialog = dialog)
        }
    }

    fun onDonateItemClick(method: DonationMethod) {
        _state.update { it.copy(donationSelectorDialog = null) }
        openUrlUseCase.invoke(url = method.url)
    }


    private companion object {
        const val TAG = "MoreVM"
    }
}
