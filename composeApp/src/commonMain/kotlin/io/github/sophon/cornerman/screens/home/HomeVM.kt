package io.github.sophon.cornerman.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sophon.cornerman.featureRegistry.FeatureRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * TODO: we should get the feature list from shared preferences
 *  so we should reuse the getAvailableFeaturesUseCase
 *  think about whether that usecase should return the full wrapper class or just FeatureInfo which we can use to get the full wrapper class from the Feature Registry.
 */
internal class HomeVM(
    private val featureRegistry: FeatureRegistry,
): ViewModel() {
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


    private fun loadFeatures() {
        val featureList = featureRegistry.getFeatures()
        _state.update { it.copy(composeRegisteredFeatures = featureList) }
    }
}

private const val TAG = "HomeVM"