package io.github.sophon.wikiwavu.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.wikiwavu.integration.WavuFeatureInfo
import io.github.sophon.wikiwavu.integration.model.TekkenFilters
import io.github.sophon.wikiwavu.integration.model.TekkenGroups
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class WavuWikiClient(
    game: Game,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
    scope: CoroutineScope,
): BaseWikiClient(
    game = game,
    featureInfo = WavuFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    scope = scope,
    infoLogger = { message ->
        Napier.d(tag = TAG) { message }
    }
) {
    override fun getFiltersFor(game: Game): Set<Filter> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val filters = when (game) {
            Game.Tekken8 -> TekkenFilters.getAllBinaryFilters()
            else -> emptySet()
        }
        return filters
    }

    override fun getGroupsFor(game: Game, extras: List<String>): List<Group> {
        require(game in supportedGameSet) {
            "${game.id} not supported. Supported: $supportedGameSet"
        }

        val genericGroupList = listOf(
            TekkenGroups.Heat,
            TekkenGroups.Neutral,
            TekkenGroups.Forward,
            TekkenGroups.DownForward,
            TekkenGroups.Down,
            TekkenGroups.DownBack,
            TekkenGroups.Back,
            TekkenGroups.Up,
            TekkenGroups.Motion,
            TekkenGroups.Crouch,
            TekkenGroups.WS,
        )
        val stanceGroupList = extras.map { TekkenGroups.Stance(it) }

        val groupList = genericGroupList + stanceGroupList
        return groupList
    }


    private companion object {
        const val TAG = "WavuWikiClient"
    }
}
