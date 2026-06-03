package io.github.sophon.core.feature.module

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.domain.WikiClient

class CoreFeatureRepo(
    private val coreWikiClientFactory: CoreWikiClientFactory,
) {
    private var gameClients: Map<Game, WikiClient> = emptyMap()

    fun initialize(config: Config): EmptyResult<WikiError> {
        gameClients = buildGameClients(config)
        return Result.Success(Unit)
    }

    fun getGameClients(): Map<Game, WikiClient> {
        return gameClients
    }

    fun getWikiClientFor(game: Game): WikiClient? {
        return gameClients[game]
    }

    private fun buildGameClients(config: Config): Map<Game, WikiClient> {
        val enabledGames = config.featureList
            .filter { it.isEnabled }
            .flatMap { it.supportedGameList }

        enabledGames
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .keys
            .takeIf { it.isNotEmpty() }
            ?.let { duplicates ->
                //TODO: log duplicates
            }

        val gameClients = enabledGames
            .distinct()
            .associateWith { game ->
                coreWikiClientFactory.create(game)
            }

        return gameClients
    }


    private companion object {
        const val TAG = "ModuleRepo"
    }
}