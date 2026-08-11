package io.github.sophon.dreamcancel.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.dreamcancel.data.remote.DreamCancelDataCache
import io.github.sophon.dreamcancel.integration.DreamCancelFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DreamCancelWikiClient(
    game: Game,
    private val dataCache: DreamCancelDataCache,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
) : BaseWikiClient(
    game = game,
    featureInfo = DreamCancelFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
    override suspend fun onClearCache() {
        dataCache.clear()
    }

    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        return emptySet()
    }


    private companion object {
        const val TAG = "DreamCancelWikiClient"
    }
}
