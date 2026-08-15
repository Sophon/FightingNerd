package io.github.sophon.wikidustloop.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.wikidustloop.integration.DustLoopFeatureInfo
import io.github.sophon.wikidustloop.integration.model.BBFilters
import io.github.sophon.wikidustloop.integration.model.GGFilters
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DustLoopWikiClient(
    game: Game,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
    scope: CoroutineScope,
): BaseWikiClient(
    game = game,
    featureInfo = DustLoopFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    scope = scope,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in DustLoopFeatureInfo.featureInfo.supportedGameSet) {
            "${game.id} not supported. Supported: ${DustLoopFeatureInfo.featureInfo.supportedGameSet}"
        }

        val set = when (game) {
            Game.BBCF -> BBFilters.getAllBinaryFilters()
            Game.GGST -> GGFilters.getAllBinaryFilters()
            else -> emptySet()
        }
        return set
    }


    private companion object {
        const val TAG = "DustLoopWikiClient"
    }
}