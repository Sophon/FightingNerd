package io.github.sophon.core.featureConfig

import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.model.WikiClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class CoreWikiClientFactory(
    private val dbFactory: (Game) -> Pair<CharacterListDB, MoveListDB>,
): KoinComponent {
    fun create(game: Game): WikiClient {
        val (characterListDB, moveListDB) = dbFactory(game)
        val wikiClient: WikiClient = get(named(game.wiki.id)) {
            parametersOf(game, characterListDB, moveListDB)
        }
        return wikiClient
    }
}
