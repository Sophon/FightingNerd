package io.github.sophon.fightingnerd.feat.more.ui.featureSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.more.usecase.GetAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.ToggleFeatureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class FeatureSettingsVM(
    private val overlayService: OverlayService,
    private val getAvailableFeaturesUseCase: GetAvailableFeaturesUseCase,
    private val toggleFeatureUseCase: ToggleFeatureUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(FeatureSettingsState())
    val state = _state.asStateFlow()


    init {
        loadFeatures()
    }


    fun toggleFeature(featureIndex: Int, isEnabled: Boolean) {
        _state.update { current ->
            val updatedFeature = current.featureList[featureIndex].run {
                copy(
                    gameList = gameList.map { game -> game.copy(isEnabled = isEnabled) }
                )
            }
            val updatedList = current.featureList.toMutableList().apply {
                set(featureIndex, updatedFeature)
            }
            current.copy(featureList = updatedList)
        }
    }

    fun toggleGame(featureIndex: Int, gameIndex: Int, isEnabled: Boolean) {
        _state.update { current ->
            val updatedFeature = current.featureList[featureIndex].run {
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
            val updatedList = current.featureList.toMutableList().apply {
                set(featureIndex, updatedFeature)
            }
            current.copy(featureList = updatedList)
        }
    }

        viewModelScope.launch {
            toggleFeatureUseCase.invoke(
                featureName = feature.featureName,
                gameId = game.id,
                isEnabled = isEnabled,
            )
                .onSuccess {
                    _state.update { state ->
                        val updatedList = state.featureList.toMutableList().apply {
                            set(featureIndex, feature)
                        }
                        state.copy(featureList = updatedList)
                    }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "toggleGame: $error" }
                    error::class.simpleName?.let { errorName ->
                        overlayService.show(
                            Toast(message = errorName, type = Toast.Type.ERROR)
                        )
                    }
                }
        }
    }


    private fun loadFeatures() {
        viewModelScope.launch {
            getAvailableFeaturesUseCase.invoke()
                .onSuccess { featureList ->
                    _state.update { it.copy(featureList = featureList) }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { error.toString() }
                }
        }
    }


    companion object {
        private const val TAG = "SettingsVM"
    }
}