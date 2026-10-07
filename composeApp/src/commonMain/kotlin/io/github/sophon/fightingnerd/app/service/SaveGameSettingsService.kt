package io.github.sophon.fightingnerd.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.MediaPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import io.github.sophon.fightingnerd.inPort.SaveGameSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn

/**
 * Invariant: an enabled game must have clean, complete data - a disabled game that has to be downloaded again
 * is acceptable, an enabled game with partial data isn't.
 */
internal class SaveGameSettingsService(
    private val loadConfigPort: LoadConfigPort,
    private val enabledGamesPort: EnabledGamesPort,
    private val saveGameSettingsPort: SaveGameSettingsPort,
    private val configureWikiPort: ConfigureWikiPort,
    private val mediaPort: MediaPort,
    private val refreshWikiPort: RefreshWikiPort,
    private val appScope: CoroutineScope,
): SaveGameSettingsUseCase {
    override suspend fun invoke(enabledGameIdSet: Set<String>): EmptyResult<AppError> {
        val composeConfig = when (val loadResult = loadConfigPort.load()) {
            is Result.Success -> loadResult.data
            is Result.Error -> return loadResult
        }
        val previousEnabledGameIdSet = when (val enabledResult = enabledGamesPort.load()) {
            is Result.Success -> enabledResult.data
            is Result.Error -> return enabledResult
        }
        val disabledGameIdSet = (previousEnabledGameIdSet - enabledGameIdSet)
        val newlyEnabledGameIdSet = (enabledGameIdSet - previousEnabledGameIdSet)

        val result = saveGameSettingsPort.saveGameSettings(composeConfig, enabledGameIdSet)
            .flatMap { configureWikiPort.configure(composeConfig, enabledGameIdSet) }
            .onSuccess {
                wipeMedia(disabledGameIdSet)
                download(newlyEnabledGameIdSet)
            }
        return result
    }

    private suspend fun wipeMedia(gameIdSet: Set<String>) {
        gameIdSet.forEach { gameId ->
            mediaPort.wipe(gameId).onError { error ->
                Napier.w(tag = TAG) { "Media wipe failed for $gameId, the game stays disabled: $error" }
            }
        }
    }

    private fun download(gameIdSet: Set<String>) {
        if (gameIdSet.isEmpty()) return
        refreshWikiPort.refresh(gameIdSet).launchIn(appScope)
    }


    private companion object {
        const val TAG = "SaveGameSettingsService"
    }
}
