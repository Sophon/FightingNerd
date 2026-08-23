package io.github.sophon.wikimizuumi.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.wikimizuumi.data.remote.MizuumiDataCache
import io.github.sophon.wikimizuumi.integration.MizuumiFeatureInfo
import io.github.sophon.wikimizuumi.integration.model.MBFilters
import io.github.sophon.wikimizuumi.integration.model.MBGroups
import io.github.sophon.wikimizuumi.integration.model.Normal
import io.github.sophon.wikimizuumi.integration.model.Special
import io.github.sophon.wikimizuumi.integration.model.UniFilters
import io.github.sophon.wikimizuumi.integration.model.UniGroups
import io.github.sophon.wikimizuumi.integration.model.VSAVFilters
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class MizuumiWikiClient(
    game: Game,
    private val dataCache: MizuumiDataCache,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
    scope: CoroutineScope,
): BaseWikiClient(
    game = game,
    featureInfo = MizuumiFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    scope = scope,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {

    override suspend fun onClearCache() {
        dataCache.clear()
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in featureInfo.supportedGameSet) {
            "${game.id} not supported. Supported: $featureInfo"
        }

        val set = when (game) {
            Game.MBTL -> MBFilters.getAllBinaryFilters()
            Game.Uni2 -> UniFilters.getAllBinaryFilters()
            Game.VSAV -> VSAVFilters.getAllBinaryFilters()
            else -> emptySet()
        }

        return set
    }

    override fun getGroupsFor(game: Game, extras: List<String>): List<Group> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val groups = when (game) {
            Game.MBTL -> listOf(
                Normal,
                MBGroups.Universal,
                Special,
                MBGroups.Super,
            )
            Game.Uni2 -> listOf(
                UniGroups.Normal,
                UniGroups.Universal,
                UniGroups.Special,
                UniGroups.Super,
            )
            Game.VSAV -> listOf(
                Normal,
                Special,
            )
            else -> emptyList()
        }

        return groups
    }


    private companion object {
        const val TAG = "MizuumiWikiClient"
    }
}
