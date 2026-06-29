package io.github.sophon.core.featureConfig

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.WikiClient

interface CoreFeatureRepo {
    fun initialize(config: Config): EmptyResult<WikiError>
    fun getGameClients(): Map<Game, WikiClient>
    fun getOtherFeatures(): List<Config.Feature>
    fun getEnabledFeatureNames(): Set<String>
    fun getWikiClientFor(game: Game): WikiClient?
}

internal class CoreFeatureRepoImpl(
    private val coreWikiClientFactory: CoreWikiClientFactory,
) : CoreFeatureRepo {
    private var gameClients: LinkedHashMap<Game, WikiClient> = linkedMapOf()
    private var otherFeatures: List<Config.Feature> = emptyList()
    private var enabledFeatureNames: Set<String> = emptySet()

    override fun initialize(config: Config): EmptyResult<WikiError> {
        gameClients = buildGameClients(config)
        otherFeatures = loadNonGameFeatures(config)
        enabledFeatureNames = config.featureList
            .filter { it.isEnabled }
            .map { it.name }
            .toSet()
        return Result.Success(Unit)
    }

    override fun getGameClients(): Map<Game, WikiClient> {
        return gameClients
    }

    override fun getOtherFeatures(): List<Config.Feature> {
        return otherFeatures
    }

    override fun getEnabledFeatureNames(): Set<String> {
        return enabledFeatureNames
    }

    override fun getWikiClientFor(game: Game): WikiClient? {
        return gameClients[game]
    }

    private fun buildGameClients(config: Config): LinkedHashMap<Game, WikiClient> {
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
            .associateWithTo(linkedMapOf()) { game ->
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