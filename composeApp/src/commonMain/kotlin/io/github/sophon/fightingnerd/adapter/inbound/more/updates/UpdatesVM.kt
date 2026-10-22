package io.github.sophon.fightingnerd.adapter.inbound.more.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fightingnerd.composeapp.generated.resources.Res
import fightingnerd.composeapp.generated.resources.home_refresh_success
import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.adapter.inbound.more.toUiUpdatesFeatureList
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.OverlayService
import io.github.sophon.fightingnerd.adapter.inbound.coreUi.Toast
import io.github.sophon.fightingnerd.inPort.SetUpdatePeriodUseCase
import io.github.sophon.fightingnerd.inPort.StartRefreshUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToLastUpdatesUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToRefreshEventsUseCase
import io.github.sophon.fightingnerd.inPort.SubscribeToUpdatePeriodUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class UpdatesVM(
    private val overlayService: OverlayService,
    private val subscribeToLastUpdatesUseCase: SubscribeToLastUpdatesUseCase,
    private val subscribeToUpdatePeriodUseCase: SubscribeToUpdatePeriodUseCase,
    private val setUpdatePeriodUseCase: SetUpdatePeriodUseCase,
    private val startRefreshUseCase: StartRefreshUseCase,
    private val subscribeToRefreshEventsUseCase: SubscribeToRefreshEventsUseCase,
): ViewModel() {
    private val _state = MutableStateFlow(UpdatesState())
    val state = flow {
        coroutineScope {
            launch { subscribeToFeatureList() }
            launch { subscribeToAutoUpdateSetting() }
            launch { subscribeToRefreshEvents() }
            emitAll(_state)
        }
    }.stateIn(
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

        startRefreshUseCase(gameIdSet)
    }

    fun refreshGame(gameId: String) {
        startRefreshUseCase(setOf(gameId))
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


    private suspend fun subscribeToFeatureList() {
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

    private suspend fun subscribeToAutoUpdateSetting() {
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

    /**
     * Every refresh, not just the ones started here - first launch, newly added games, background updates.
     */
    private suspend fun subscribeToRefreshEvents() {
        subscribeToRefreshEventsUseCase().collect { event ->
            when (event) {
                is RefreshEvent.Started -> setGameRefreshing(gameId = event.game.id, isRefreshing = true)
                is RefreshEvent.Progress -> Unit
                is RefreshEvent.Finished -> {
                    setGameRefreshing(gameId = event.game.id, isRefreshing = false)
                    overlayService.show(
                        Toast(
                            message = getString(
                                Res.string.home_refresh_success,
                                event.game.displayName,
                                event.successCount,
                            ),
                            type = Toast.Type.SUCCESS,
                        )
                    )
                }
                is RefreshEvent.Failure -> {
                    Napier.e(tag = TAG) { "refresh ${event.game.id}: ${event.error}" }
                    overlayService.show(event.error)
                }
            }
        }
    }


    companion object {
        private const val TAG = "UpdatesVM"
    }
}
