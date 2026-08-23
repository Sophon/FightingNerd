package io.github.sophon.dreamcancel.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.dreamcancel.data.remote.DreamCancelDataCache
import io.github.sophon.dreamcancel.integration.COTWGroups
import io.github.sophon.dreamcancel.integration.DreamCancelFeatureInfo
import io.github.sophon.dreamcancel.integration.KofGroups
import io.github.sophon.dreamcancel.integration.Normal
import io.github.sophon.dreamcancel.integration.Special
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class DreamCancelWikiClient(
    game: Game,
    private val dataCache: DreamCancelDataCache,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
    scope: CoroutineScope,
) : BaseWikiClient(
    game = game,
    featureInfo = DreamCancelFeatureInfo.featureInfo,
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
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        return emptySet()
    }

    override fun getGroupsFor(game: Game, extras: List<String>): List<Group> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val groups = when (game) {
            Game.KoFXV -> listOf(
                Normal,
                KofGroups.Rush,
                KofGroups.Throw,
                Special,
                KofGroups.Climax
            )
            Game.COTW -> listOf(
                Normal,
                COTWGroups.Combination,
                COTWGroups.Throw,
                COTWGroups.Rev,
                COTWGroups.FeintDodge,
                Special,
                COTWGroups.HiddenGear,
            )
            else -> emptyList()
        }

        return groups
    }


    private companion object {
        const val TAG = "DreamCancelWikiClient"
    }
}
