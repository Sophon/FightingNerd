package io.github.sophon.wikimizuumi.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.wikimizuumi.data.remote.MizuumiDataCache
import io.github.sophon.wikimizuumi.integration.MizuumiFeatureInfo
import io.github.sophon.wikimizuumi.integration.model.MBFilters
import io.github.sophon.wikimizuumi.integration.model.UniFilters
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


    private companion object {
        const val TAG = "MizuumiWikiClient"
    }
}
