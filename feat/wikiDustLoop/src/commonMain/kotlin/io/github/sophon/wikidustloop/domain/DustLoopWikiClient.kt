package io.github.sophon.wikidustloop.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.wikidustloop.integration.DustLoopFeatureInfo
import io.github.sophon.wikidustloop.integration.model.BBCFGroups
import io.github.sophon.wikidustloop.integration.model.BBFilters
import io.github.sophon.wikidustloop.integration.model.DBFZGroups
import io.github.sophon.wikidustloop.integration.model.GBVSRGroups
import io.github.sophon.wikidustloop.integration.model.GGFilters
import io.github.sophon.wikidustloop.integration.model.GGSTGroups
import io.github.sophon.wikidustloop.integration.model.MTFSGroups
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

    override fun getGroupsFor(game: Game, extras: List<String>): List<Group> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val groups = when (game) {
            Game.GGST -> listOf(
                GGSTGroups.Normal,
                GGSTGroups.Universal,
                GGSTGroups.Special,
                GGSTGroups.Super,
            )
            Game.BBCF -> listOf(
                BBCFGroups.Normal,
                BBCFGroups.Universal,
                BBCFGroups.Special,
                BBCFGroups.Super,
                BBCFGroups.Exceed,
                BBCFGroups.Astral,
            )
            Game.DBFZ -> listOf(
                DBFZGroups.Normal,
                DBFZGroups.Special,
                DBFZGroups.Assist,
                DBFZGroups.Super,
            )
            Game.GBVSR -> listOf(
                GBVSRGroups.Normal,
                GBVSRGroups.Universal,
                GBVSRGroups.Special,
                GBVSRGroups.Unique,
                GBVSRGroups.Super,
            )
            Game.MTFS -> listOf(
                MTFSGroups.Normal,
                MTFSGroups.Special,
                MTFSGroups.Unique,
                MTFSGroups.Assist,
                MTFSGroups.Super,
                MTFSGroups.Tokon,
            )
            else -> emptyList()
        }
        return groups
    }


    private companion object {
        const val TAG = "DustLoopWikiClient"
    }
}