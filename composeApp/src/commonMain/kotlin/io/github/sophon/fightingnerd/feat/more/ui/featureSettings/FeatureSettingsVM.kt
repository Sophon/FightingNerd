package io.github.sophon.fightingnerd.feat.more.ui.featureSettings

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

internal class FeatureSettingsVM(
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
    private val toggleFeatureUseCase: ToggleFeatureUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(FeatureSettingsState())
    val state = _state
        .onStart {
            loadFeatures()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FeatureSettingsState(),
        )

    fun toggleFeature(featureIndex: Int, isEnabled: Boolean) {
        //TODO: updated DataStore

        _state.update { state ->
            val feature = state.featureList[featureIndex].run {
                copy(
                    gameList = gameList.map { game -> game.copy(isEnabled = isEnabled) }
                )
            }
            val updatedList = state.featureList.toMutableList().apply {
                set(featureIndex, feature)
            }
            state.copy(featureList = updatedList)
        }
    }

    fun toggleGame(featureIndex: Int, gameIndex: Int, isEnabled: Boolean) {
        //TODO: update DataStore

        _state.update { state ->
            val feature = state.featureList[featureIndex].run {
                copy(
                    gameList = gameList.mapIndexed { index, game ->
                        if (index == gameIndex) {
                            game.copy(isEnabled = isEnabled)
                        } else {
                            game
                        }
                    }
                )
            }
            val updatedList = state.featureList.toMutableList().apply {
                set(featureIndex, feature)
            }
            state.copy(featureList = updatedList)
        }
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