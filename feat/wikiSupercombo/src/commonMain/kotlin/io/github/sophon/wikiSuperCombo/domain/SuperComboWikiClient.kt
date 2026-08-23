package io.github.sophon.wikiSuperCombo.domain

import io.github.aakira.napier.Napier
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.wiki.data.CharacterRepo
import io.github.sophon.core.wiki.data.MoveRepo
import io.github.sophon.core.wiki.domain.BaseWikiClient
import io.github.sophon.core.wiki.model.Filter
import io.github.sophon.core.wiki.model.Group
import io.github.sophon.wikiSuperCombo.integration.SuperComboFeatureInfo
import kotlinx.coroutines.CoroutineScope
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class SuperComboWikiClient(
    game: Game,
    characterRepo: CharacterRepo,
    moveRepo: MoveRepo,
    scope: CoroutineScope,
): BaseWikiClient(
    game = game,
    featureInfo = SuperComboFeatureInfo.featureInfo,
    characterRepo = characterRepo,
    moveRepo = moveRepo,
    scope = scope,
    infoLogger = { Napier.i(tag = TAG) { it } },
    debugLogger = { Napier.d(tag = TAG) { it } },
) {
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
            Game.StreetFighter6 -> listOf(
                SFGroups.Normal,
                SFGroups.Throw,
                SFGroups.Special,
                SFGroups.Drive,
                SFGroups.Super,
                SFGroups.Taunt,
            )
            Game.AVL -> listOf(
                AVLGroups.Normal,
                AVLGroups.Special,
                AVLGroups.Flow,
                AVLGroups.Super,
            )
            else -> emptyList()
        }
        return groups
    }


    private companion object {
        const val TAG = "SuperComboWikiClient"
    }
}
