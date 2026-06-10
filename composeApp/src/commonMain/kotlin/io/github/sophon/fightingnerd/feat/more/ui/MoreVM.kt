package io.github.sophon.fightingnerd.feat.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.feat.more.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.ToggleFeatureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class MoreVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
    private val toggleFeatureUseCase: ToggleFeatureUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(MoreState())
    val state = _state
        .onStart {
            loadFeatures()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MoreState(),
        )

    fun toggleFeature(index: Int) {
//        val feature = _state.value.featureList.getOrNull(index)
//        if (feature == null) {
//            Napier.e(tag = TAG) { "$index: out of bounds" }
//            return
//        }
//        val newEnabledState = feature.isEnabled.not()
//
//        viewModelScope.launch {
//            toggleFeatureUseCase.invoke(featureInfo = feature.featureInfo, isEnabled = newEnabledState)
//                .onSuccess {
//                    _state.update {
//                        it.copy(
//                            featureList = it.featureList.mapIndexed { i, feature ->
//                                if (i == index) feature.copy(isEnabled = newEnabledState)
//                                else feature
//                            }
//                        )
//                    }
//                }
//                .onError { Napier.e(tag = TAG) { it.toString() } }
//        }
    }


    private fun loadFeatures() {
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