package io.github.sophon.fightingnerd.feat.module

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.Config
import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.feat.module.domain.WikiClientFactory
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase

internal class ModuleRepo(
    private val loadConfigUseCase: LoadConfigUseCase,
    private val wikiClientFactory: WikiClientFactory,
) {
    private var gameClients: Map<Game, WikiClient> = emptyMap()

    suspend fun initialize(): EmptyResult<AppError> {
        val result = loadConfigUseCase.invoke()
            .map { config ->
                gameClients = buildGameClients(config)
            }
        return result
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
                Napier.w(tag = TAG) { "Duplicate games in config: $duplicates" }
            }

        val gameClients = enabledGames
            .distinct()
            .associateWith { game ->
                wikiClientFactory.create(game)
            }

        return gameClients
    }


    private companion object {
        const val TAG = "ModuleRepo"
    }
}
