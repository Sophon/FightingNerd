package io.github.sophon.wikidragdown.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.wikidragdown.integration.DragDownFeatureInfo
import kotlinx.coroutines.CoroutineScope

internal class DragDownWikiClient(
    game: Game,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
    scope: CoroutineScope,
): BaseWikiClient(
    game = game,
    featureInfo = DragDownFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    scope = scope,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in featureInfo.supportedGameSet) {
            "${game.id} not supported. Supported: ${featureInfo.supportedGameSet}"
        }

        return emptySet()
    }


    private companion object {
        const val TAG = "DragDownWikiClient"
    }
}
