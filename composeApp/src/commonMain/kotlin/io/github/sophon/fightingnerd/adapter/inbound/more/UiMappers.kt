package io.github.sophon.fightingnerd.adapter.inbound.more

import io.github.sophon.core.util.toHumanReadableString
import io.github.sophon.fightingnerd.adapter.inbound.more.featureSettings.FeatureSettingsState
import io.github.sophon.fightingnerd.adapter.inbound.more.updates.UpdatesState
import io.github.sophon.fightingnerd.app.model.Game
import io.github.sophon.fightingnerd.app.model.Wiki
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.time.Instant

internal fun Map<Game, Boolean>.toUiFeatureSettingList(): ImmutableList<FeatureSettingsState.UiFeatureSetting> {
    val uiFeatureList = entries
        .groupBy { (game, _) -> game.wiki }
        .map { (wiki, entryList) ->
            val uiFeature = FeatureSettingsState.UiFeatureSetting(
                featureName = wiki.name,
                iconUrl = wiki.iconUrl,
                gameList = entryList
                    .map { (game, isEnabled) ->
                        FeatureSettingsState.UiFeatureSetting.UiGame(
                            displayName = game.displayName,
                            id = game.id,
                            isEnabled = isEnabled,
                        )
                    }
                    .toImmutableList(),
            )
            uiFeature
        }
        .toImmutableList()
    return uiFeatureList
}

/**
 * Games that were never downloaded have nothing to show, and neither do wikis left without games.
 */
internal fun Map<Game, Instant?>.toUiUpdatesFeatureList(
    refreshingGameIdSet: Set<String>,
): ImmutableList<UpdatesState.UiFeatureSetting> {
    val uiFeatureList = entries
        .mapNotNull { (game, lastUpdate) -> lastUpdate?.let { game to lastUpdate } }
        .groupBy { (game, _) -> game.wiki }
        .map { (wiki, gameList) -> wiki.toUiUpdatesFeature(gameList, refreshingGameIdSet) }
        .toImmutableList()
    return uiFeatureList
}

private fun Wiki.toUiUpdatesFeature(
    gameList: List<Pair<Game, Instant>>,
    refreshingGameIdSet: Set<String>,
): UpdatesState.UiFeatureSetting {
    val uiFeature = UpdatesState.UiFeatureSetting(
        name = name,
        iconUrl = iconUrl,
        gameList = gameList
            .map { (game, lastUpdate) ->
                UpdatesState.UiFeatureSetting.UiGame(
                    name = game.displayName,
                    id = game.id,
                    lastUpdatedTimeStamp = lastUpdate.toHumanReadableString(),
                    isRefreshing = (game.id in refreshingGameIdSet),
                )
            }
            .toImmutableList(),
    )
    return uiFeature
}
