package io.github.sophon.core.featureConfig

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.model.WikiClient

interface FeatureRepo {
    fun initialize(config: Config): EmptyResult<WikiError>
    fun getGameClients(): Map<Game, WikiClient>
    fun getOtherFeatures(): List<Config.Feature>
    fun getEnabledFeatureNames(): Set<String>
    fun getWikiClientFor(game: Game): WikiClient?
}
