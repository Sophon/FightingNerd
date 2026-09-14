package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.KEY_HAS_LAUNCHED_BEFORE
import io.github.sophon.fightingnerd.core.usecase.RefreshUseCase
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

internal class PerformFirstTimeConfigUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
    private val refreshUseCase: RefreshUseCase,
) {
    internal suspend operator fun invoke() {
        val hasLaunchedBeforeKey = booleanPreferencesKey(KEY_HAS_LAUNCHED_BEFORE)
        val snapshot = store.data.first()
        if (snapshot[hasLaunchedBeforeKey] == true) return

        store.edit { prefs ->
            featureRepo.getGameClients().forEach { (game, wikiClient) ->
                prefs[featureKey(wikiClient.featureInfo.name, game.id)] = ENABLED_GAMES_FIRST_TIME.contains(game)
            }
            prefs[hasLaunchedBeforeKey] = true
        }

        refreshUseCase().collect()
    }

    private companion object {
        val ENABLED_GAMES_FIRST_TIME = listOf(
            Game.StreetFighter6,
            Game.Tekken8,
            Game.GGST,
        )
    }
}
