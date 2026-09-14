package io.github.sophon.fightingnerd.feat.more.ui.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.util.toHumanReadableString
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.feat.more.usecase.ManualRefreshUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToAvailableFeaturesUseCase
import io.github.sophon.fightingnerd.feat.more.usecase.SubscribeToUpdatePeriodUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class UpdatesVM(
    private val overlayService: OverlayService,
    private val subscribeToAvailableFeaturesUseCase: SubscribeToAvailableFeaturesUseCase,
    private val subscribeToUpdatePeriodUseCase: SubscribeToUpdatePeriodUseCase,
    private val setUpdatePeriodUseCase: SetUpdatePeriodUseCase,
    private val manualRefreshUseCase: ManualRefreshUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(UpdatesState())
    val state = _state
        .onStart {
            subscribeToFeatureList()
            subscribeToAutoUpdateSetting()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UpdatesState(),
        )


    fun toggleEnableAutoUpdate(isEnabled: Boolean) {
        _state.update { state ->
            state.copy(updatedAutoUpdateSettings = state.updatedAutoUpdateSettings.copy(isEnabled = isEnabled))
        }
    }

    fun setPeriod(duration: String) {
        _state.update { current ->
            val parsed = duration.toIntOrNull()
            current.copy(updatedAutoUpdateSettings = current.updatedAutoUpdateSettings.copy(period = parsed))
        }
    }

    fun setUnit(index: Int) {
        _state.update { current ->
            val newUnit = UpdatesState.AutoUpdateSettings.TimeUnit.entries[index]
            val updated = current.updatedAutoUpdateSettings.copy(unit = newUnit)
            current.copy(updatedAutoUpdateSettings = updated)
        }
    }

    fun refreshWiki(name: String) {
        val gameIdList = _state.value.featureList
            .firstOrNull { it.name == name }
            ?.gameList
            ?.map { it.id }
            .orEmpty()
        if (gameIdList.isEmpty()) return

        setFeatureRefreshing(name = name, isRefreshing = true)
        viewModelScope.launch {
            manualRefreshUseCase.refreshWiki(gameIdList)
                .onSuccess {
                    overlayService.show(Toast(message = "Refreshed", type = Toast.Type.SUCCESS))
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "refreshWiki $name: $error" }
                    overlayService.show(error)
                }
            setFeatureRefreshing(name = name, isRefreshing = false)
        }
    }

    fun refreshGame(gameId: String) {
        setGameRefreshing(gameId = gameId, isRefreshing = true)
        viewModelScope.launch {
            manualRefreshUseCase.refreshGame(gameId)
                .onSuccess {
                    overlayService.show(Toast(message = "Refreshed", type = Toast.Type.SUCCESS))
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "refreshGame $gameId: $error" }
                    overlayService.show(error)
                }
            setGameRefreshing(gameId = gameId, isRefreshing = false)
        }
    }

    private fun setFeatureRefreshing(name: String, isRefreshing: Boolean) {
        _state.update { current ->
            val newList = current.featureList.map { feature ->
                if (feature.name != name) return@map feature
                val newGameList = feature.gameList
                    .map { it.copy(isRefreshing = isRefreshing) }
                    .toImmutableList()
                feature.copy(gameList = newGameList)
            }.toImmutableList()
            current.copy(featureList = newList)
        }
    }

    private fun setGameRefreshing(gameId: String, isRefreshing: Boolean) {
        _state.update { current ->
            val newList = current.featureList.map { feature ->
                val newGameList = feature.gameList.map { game ->
                    if (game.id == gameId) game.copy(isRefreshing = isRefreshing) else game
                }.toImmutableList()
                feature.copy(gameList = newGameList)
            }.toImmutableList()
            current.copy(featureList = newList)
        }
    }

    fun save() {
        val settings = _state.value.updatedAutoUpdateSettings
        val duration = settings.toDuration()
        if (settings.isEnabled && duration == null) return

        viewModelScope.launch {
            val period = duration.takeIf { settings.isEnabled }
            setUpdatePeriodUseCase(period)
                .onSuccess {
                    overlayService.show(
                        Toast(message = "Saved", type = Toast.Type.SUCCESS)
                    )
                }
                .onError { error ->
                    Napier.e(tag = TAG) { "setUpdatePeriod: $error" }
                    overlayService.show(error)
                }
        }
    }


    private fun subscribeToFeatureList() {
        viewModelScope.launch {
            subscribeToAvailableFeaturesUseCase.invoke().collect { result ->
                result
                    .onSuccess { featureList ->
                        _state.update { current ->
                            val refreshingGames = current.featureList
                                .flatMap { it.gameList }
                                .filter { it.isRefreshing }
                                .map { it.id }
                                .toSet()

                            val uiList = featureList
                                .mapNotNull { feature ->
                                    val enabledGames = feature.gameList
                                        .mapNotNull { game ->
                                            val timestamp = game.lastUpdatedTimeStamp
                                            if (game.isEnabled.not() || timestamp == null) return@mapNotNull null

                                            val uiGame = UpdatesState.UiFeatureSetting.UiGame(
                                                name = game.name,
                                                id = game.id,
                                                lastUpdatedTimeStamp = timestamp.toHumanReadableString(),
                                                isRefreshing = game.id in refreshingGames,
                                            )
                                            uiGame
                                        }
                                    if (enabledGames.isEmpty()) return@mapNotNull null
                                    val uiFeature = UpdatesState.UiFeatureSetting(
                                        name = feature.name,
                                        iconUrl = feature.iconUrl,
                                        version = feature.version,
                                        gameList = enabledGames.toImmutableList(),
                                    )
                                    uiFeature
                                }
                                .toImmutableList()
                            current.copy(featureList = uiList)
                        }
                    }
                    .onError { error ->
                        Napier.e(tag = TAG) { "subscribeToFeatureList: $error" }
                    }
            }
        }
    }

    private fun subscribeToAutoUpdateSetting() {
        viewModelScope.launch {
            subscribeToUpdatePeriodUseCase().collect { duration ->
                _state.update { current ->
                    val newSettings = if (duration == null) {
                        current.updatedAutoUpdateSettings.copy(isEnabled = false)
                    } else {
                        UpdatesState.AutoUpdateSettings.fromDuration(duration)
                    }
                    current.copy(
                        currentAutoUpdateSettings = newSettings,
                        updatedAutoUpdateSettings = newSettings,
                    )
                }
            }
        }
    }


    companion object {
        private const val TAG = "UpdatesVM"
    }
}
