package io.github.sophon.fightingnerd.adapter.inbound.more.featureSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.adapter.inbound.more.toUiFeatureSettingList
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.Dialog
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.OverlayService
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.Toast
import io.github.sophon.fightingnerd.inPort.SaveGameSettingsUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToGameSettingsUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class FeatureSettingsVM(
    private val overlayService: OverlayService,
    private val subscribeToGameSettingsUseCase: SubscribeToGameSettingsUseCase,
    private val saveGameSettingsUseCase: SaveGameSettingsUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(FeatureSettingsState())
    val state = _state.asStateFlow()

    private var saveJob: Job? = null


    init {
        loadFeatures()
    }


    fun toggleFeature(featureIndex: Int, isEnabled: Boolean) {
        _state.update { current ->
            val updatedFeature = current.updatedFeatureList[featureIndex].run {
                copy(
                    gameList = gameList
                        .map { game -> game.copy(isEnabled = isEnabled) }
                        .toImmutableList()
                )
            }
            val updatedList = current.updatedFeatureList
                .toMutableList()
                .apply {
                    set(featureIndex, updatedFeature)
                }
                .toImmutableList()

            val currentFeature = current.currentFeatureList[featureIndex]
            val featureGameIds = currentFeature.gameList.map { it.id }.toSet()
            val cleared = current.gamesToBeDisabled.filter { it.id !in featureGameIds }
            val newlyDisabled = if (isEnabled) {
                emptyList()
            } else {
                currentFeature.gameList.filter { it.isEnabled }
            }
            val updatedGamesToBeDisabled = (cleared + newlyDisabled).toImmutableList()

            current.copy(
                updatedFeatureList = updatedList,
                gamesToBeDisabled = updatedGamesToBeDisabled,
            )
        }
    }

    fun toggleGame(featureIndex: Int, gameIndex: Int, isEnabled: Boolean) {
        _state.update { current ->
            val updatedFeature = current.updatedFeatureList[featureIndex].run {
                val updatedGameList = gameList
                    .toMutableList()
                    .apply {
                        set(gameIndex, get(gameIndex).copy(isEnabled = isEnabled))
                    }
                    .toImmutableList()
                copy(gameList = updatedGameList)
            }
            val updatedList = current.updatedFeatureList
                .toMutableList()
                .apply {
                    set(featureIndex, updatedFeature)
                }
                .toImmutableList()

            val updatedGamesToBeDisabled = updateForGameToggle(
                currentGamesToBeDisabled = current.gamesToBeDisabled,
                currentGame = current.currentFeatureList[featureIndex].gameList[gameIndex],
                becomesEnabled = isEnabled,
            )

            current.copy(
                updatedFeatureList = updatedList,
                gamesToBeDisabled = updatedGamesToBeDisabled,
            )
        }
    }

    fun displayConfirmationDialog() {
        if (saveJob?.isActive == true) return

        val gamesToBeDisabled = _state.value.gamesToBeDisabled
        if (gamesToBeDisabled.isEmpty()) {
            commitSave()
        } else {
            overlayService.show(
                Dialog { onDismiss ->
                    ConfirmFeatureChangeDialog(
                        gameList = gamesToBeDisabled,
                        onConfirm = {
                            commitSave()
                            onDismiss()
                        },
                        onDismiss = onDismiss,
                    )
                }
            )
        }
    }

    private fun commitSave() {
        if (saveJob?.isActive == true) return
        saveJob = viewModelScope.launch {
            val featureList = _state.value.updatedFeatureList
            val enabledGameIdSet = featureList
                .flatMap { feature -> feature.gameList }
                .filter { game -> game.isEnabled }
                .map { game -> game.id }
                .toSet()
            saveGameSettingsUseCase(enabledGameIdSet)
                .onSuccess {
                    _state.update {
                        it.copy(
                            currentFeatureList = featureList,
                            gamesToBeDisabled = persistentListOf(),
                        )
                    }
                    overlayService.show(
                        Toast(message = "Saved", type = Toast.Type.SUCCESS)
                    )
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "saveConfiguration: $error" }
                    overlayService.show(error)
                }
        }
    }

    private fun loadFeatures() {
        viewModelScope.launch {
            subscribeToGameSettingsUseCase().first()
                .onSuccess { isEnabledByGame ->
                    val list = isEnabledByGame.toUiFeatureSettingList()
                    _state.update { it.copy(currentFeatureList = list, updatedFeatureList = list) }
                }
                .onError { error ->
                    Napier.e(tag = TAG) { error.toString() }
                }
        }
    }

    private fun updateForGameToggle(
        currentGamesToBeDisabled: ImmutableList<FeatureSettingsState.UiFeatureSetting.UiGame>,
        currentGame: FeatureSettingsState.UiFeatureSetting.UiGame,
        becomesEnabled: Boolean,
    ): ImmutableList<FeatureSettingsState.UiFeatureSetting.UiGame> {
        val result = when {
            becomesEnabled -> currentGamesToBeDisabled.filter { it.id != currentGame.id }
            currentGame.isEnabled -> currentGamesToBeDisabled + currentGame
            else -> currentGamesToBeDisabled
        }.toImmutableList()

        return result
    }


    companion object {
        private const val TAG = "FeatureSettingsVM"
    }
}