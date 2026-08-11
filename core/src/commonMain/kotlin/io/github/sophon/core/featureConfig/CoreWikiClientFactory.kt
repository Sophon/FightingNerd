package io.github.sophon.core.featureConfig

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.model.WikiClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class CoreWikiClientFactory : KoinComponent {
    fun create(game: Game): WikiClient {
        val wikiClient: WikiClient = get(named(game.wiki.id)) {
            parametersOf(game)
        }
        return wikiClient
    }
}
