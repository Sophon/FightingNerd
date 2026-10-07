package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.ConfigureWikiPort
import io.github.sophon.fightingnerd.app.outPort.FirstLaunchPort
import io.github.sophon.fightingnerd.app.outPort.SubscribeToAvailableGamesPort
import io.github.sophon.fightingnerd.app.outPort.RefreshWikiPort
import io.github.sophon.fightingnerd.app.outPort.SaveGameSettingsPort
import io.github.sophon.fightingnerd.inPort.FirstTimeConfigUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

internal class FirstTimeConfigService(
    private val firstLaunchPort: FirstLaunchPort,
    private val subscribeToAvailableGamesPort: SubscribeToAvailableGamesPort,
    private val saveGameSettingsPort: SaveGameSettingsPort,
    private val configureWikiPort: ConfigureWikiPort,
    private val refreshWikiPort: RefreshWikiPort,
): FirstTimeConfigUseCase {
    override suspend fun invoke(): EmptyResult<AppError> {
        val result = firstLaunchPort.hasLaunchedBefore()
            .flatMap { hasLaunchedBefore ->
                if (hasLaunchedBefore) {
                    Result.Success(Unit)
                } else {
                    configureFirstLaunch()
                }
            }
        return result
    }


    private suspend fun configureFirstLaunch(): EmptyResult<AppError> {
        val availableGameSet = subscribeToAvailableGamesPort.subscribeToAvailableGames().first()
        val enabledGameSet = availableGameSet
            .filter { game -> game.id in DEFAULT_ENABLED_GAME_ID_SET }
            .toSet()
        val isEnabledByGame = availableGameSet.associateWith { game -> game in enabledGameSet }

        val result = saveGameSettingsPort.saveGameSettings(isEnabledByGame)
            .flatMap { configureWikiPort.configure(availableGameSet, enabledGameSet) }
            .flatMap { firstLaunchPort.markLaunched() }
            .onSuccess { refreshWikiPort.refresh().collect() }
        return result
    }
}


private val DEFAULT_ENABLED_GAME_ID_SET = setOf(
    "Street_Fighter_6",
    "Tekken_8",
    "GGST",
)
