package io.github.sophon.xko.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterListDB
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveListDB
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.core.wiki.data.toDomainError
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.xko.data.XkoWikiDataSource
import io.github.sophon.xko.data.remote.XkoDataCache
import io.github.sophon.xko.integration.XkoFeatureInfo
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class XkoWikiClient(
    game: Game,
    @Suppress("unused") private val source: XkoWikiDataSource,
    private val dataCache: XkoDataCache,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
): BaseWikiClient(
    game = game,
    featureInfo = XkoFeatureInfo.featureInfo,
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
        const val TAG = "XkoWikiClient"
    }
}
