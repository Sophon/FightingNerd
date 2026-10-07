package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onError
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.model.RefreshEvent
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.EnabledGamesPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.LoadConfigPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import io.github.sophon.fightingnerd.inPort.OnLaunchSetupUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal class OnLaunchSetupService(
    private val loadConfigPort: LoadConfigPort,
    private val firstLaunchPort: FirstLaunchPort,
    private val saveGameSettingsPort: SaveGameSettingsPort,
    private val enabledGamesPort: EnabledGamesPort,
    private val configureWikiPort: ConfigureWikiPort,
    private val refreshWikiPort: RefreshWikiPort,
): OnLaunchSetupUseCase {
    override suspend fun invoke(): Result<Flow<RefreshEvent>, AppError> {
        val composeConfig = when (val loadResult = loadConfigPort.load()) {
            is Result.Success -> loadResult.data
            is Result.Error -> return loadResult
        }

        val hasLaunchedBefore = when (val hasLaunchedBeforeResult = firstLaunchPort.hasLaunchedBefore()) {
            is Result.Success -> {
                val hasLaunchedBefore = hasLaunchedBeforeResult.data
                if (hasLaunchedBefore.not()) {
                    saveGameSettingsPort.saveGameSettings(
                        composeConfig = composeConfig,
                        enabledGameIdSet = DEFAULT_ENABLED_GAME_ID_SET,
                    )
                        .flatMap { firstLaunchPort.markLaunched() }
                        .onError { return Result.Error(it) }
                }
                hasLaunchedBefore
            }
            is Result.Error -> return hasLaunchedBeforeResult
        }

        val result = enabledGamesPort.load()
            .flatMap { enabledGameIdSet ->
                configureWikiPort.configure(composeConfig, enabledGameIdSet)
            }
            .map {
                if (hasLaunchedBefore) {
                    emptyFlow()
                } else{
                    refreshWikiPort.refresh()
                }
            }
        return result
    }
}


private val DEFAULT_ENABLED_GAME_ID_SET = setOf(
    "Street_Fighter_6",
    "Tekken_8",
    "GGST",
)
