package io.github.sophon.cornerman.screens.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.onError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.cornerman.screens.settings.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.cornerman.screens.settings.usecase.ToggleFeatureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SettingsVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
    private val toggleFeatureUseCase: ToggleFeatureUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(SettingsViewState())
    val state = _state
        .onStart {
            loadFeatures()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsViewState(),
        )

    fun toggleFeature(index: Int) {
        val feature = _state.value.featureList.getOrNull(index)
        if (feature == null) {
            Napier.e(tag = TAG) { "$index: out of bounds" }
            return
        }
        val newEnabledState = feature.isEnabled.not()

        viewModelScope.launch {
            toggleFeatureUseCase.invoke(feature = feature.feature, isEnabled = newEnabledState)
                .onSuccess {
                    _state.update {
                        it.copy(
                            featureList = it.featureList.mapIndexed { i, feature ->
                                if (i == index) feature.copy(isEnabled = newEnabledState)
                                else feature
                            }
                        )
                    }
                }
                .onError { Napier.e(tag = TAG) { it.toString() } }
        }
    }


    private suspend fun loadFeatures() {
        getAvailableFeaturesUseCase.invoke()
            .onSuccess { featureList ->
                _state.update { it.copy(featureList = featureList) }
            }
            .onError { error ->
                Napier.e(tag = TAG) { error.toString() }
            }
    }

    companion object {
        private const val TAG = "SettingsVM"
    }
}