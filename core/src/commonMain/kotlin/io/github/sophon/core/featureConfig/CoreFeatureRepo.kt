package io.github.sophon.core.featureConfig

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.WikiClient

class CoreFeatureRepo(
    private val coreWikiClientFactory: CoreWikiClientFactory,
) {
    private var gameClients: Map<Game, WikiClient> = emptyMap()
    private var otherFeatures: List<Config.Feature> = emptyList()

    fun initialize(config: Config): EmptyResult<WikiError> {
        gameClients = buildGameClients(config)
        otherFeatures = loadNonGameFeatures(config)
        return Result.Success(Unit)
    }

    fun getGameClients(): Map<Game, WikiClient> {
        return gameClients
    }

    fun getOtherFeatures(): List<Config.Feature> {
        return otherFeatures
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

    private fun loadNonGameFeatures(config: Config): List<Config.Feature> {
        val features = config.featureList.filter { it.supportedGameList.isEmpty() }
        return features
    }


    private companion object {
        const val TAG = "ModuleRepo"
    }
}