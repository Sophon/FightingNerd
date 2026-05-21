package io.github.sophon.fightingnerd.feat.module.domain

import io.github.sophon.core.feature.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.domain.WikiClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

internal class WikiClientFactory(
    private val dbFactory: (Game) -> Pair<CharacterListDB, MoveListDB>,
) : KoinComponent {
    fun create(game: Game): WikiClient {
        val (characterListDB, moveListDB) = dbFactory(game)
        return get(named(game.wiki.id)) {
            parametersOf(game.id, characterListDB, moveListDB)
        }
    }
}
