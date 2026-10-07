package io.github.sophon.fightingnerd.adapter.inbound.more.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.adapter.inbound.more.toUiUpdatesFeatureList
import io.github.sophon.fightingnerd.core.ui.OverlayService
import io.github.sophon.fightingnerd.core.ui.Toast
import io.github.sophon.fightingnerd.inPort.RefreshGamesUseCase
import io.github.sophon.fightingnerd.inPort.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToLastUpdatesUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToUpdatePeriodUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class UpdatesVM(
    private val overlayService: OverlayService,
    private val subscribeToLastUpdatesUseCase: SubscribeToLastUpdatesUseCase,
    private val subscribeToUpdatePeriodUseCase: SubscribeToUpdatePeriodUseCase,
    private val setUpdatePeriodUseCase: SetUpdatePeriodUseCase,
    private val refreshGamesUseCase: RefreshGamesUseCase,
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
        val gameIdSet = _state.value.featureList
            .firstOrNull { it.name == name }
            ?.gameList
            ?.map { it.id }
            .orEmpty()
            .toSet()
        if (gameIdSet.isEmpty()) return

        setFeatureRefreshing(name = name, isRefreshing = true)
        viewModelScope.launch {
            refreshGamesUseCase(gameIdSet)
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
            refreshGamesUseCase(setOf(gameId))
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
            subscribeToLastUpdatesUseCase().collect { result ->
                result
                    .onSuccess { lastUpdateByGame ->
                        _state.update { current ->
                            val refreshingGameIdSet = current.featureList
                                .flatMap { it.gameList }
                                .filter { it.isRefreshing }
                                .map { it.id }
                                .toSet()
                            val uiList = lastUpdateByGame.toUiUpdatesFeatureList(refreshingGameIdSet)
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
