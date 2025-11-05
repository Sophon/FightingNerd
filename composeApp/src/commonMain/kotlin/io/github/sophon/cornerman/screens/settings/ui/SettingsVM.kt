package io.github.sophon.cornerman.screens.settings.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SettingsVM(): ViewModel() {
    private val _state = MutableStateFlow<SettingsViewState>(SettingsViewState())
    val state = _state.asStateFlow()
}