package com.example.cornerman.screens.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cornerman.featureRegistry.wavuWiki.WavuWikiFeature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeVM(): ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())
    val state = _state
        .onStart {
            loadFeatures()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeViewState()
        )


    fun onSavedClick() {
        //TODO:
    }

    fun onSearchClick() {
        //TODO
    }

    fun onSettingsClick() {
        //TODO:
    }


    private fun loadFeatures() {
        val registeredFeatures = listOf(
            WavuWikiFeature(),
        )

        _state.update { it.copy(registeredFeatures = registeredFeatures) }
    }
}

private const val TAG = "HomeVM"