package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.fightingnerd.KEY_FIRST_TIME_HOME_INIT_DONE
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.flow.first

internal class CheckIfFirstLaunchUseCase(
    private val featureRepo: FeatureRepo,
    private val store: DataStore<Preferences>,
) {
    internal suspend fun invoke() {
        val firstTimeFlagKey = booleanPreferencesKey(KEY_FIRST_TIME_HOME_INIT_DONE)
        val snapshot = store.data.first()
        if (snapshot[firstTimeFlagKey] == true) return

        store.edit { prefs ->
            featureRepo.getGameClients().forEach { (game, wikiClient) ->
                prefs[featureKey(wikiClient.featureInfo.name, game.id)] = ENABLED_GAMES_FIRST_TIME.contains(game)
            }
            prefs[firstTimeFlagKey] = true
        }
    }

    private companion object {
        val ENABLED_GAMES_FIRST_TIME = listOf(
            Game.StreetFighter6,
            Game.Tekken8,
            Game.GGST,
        )
    }
}
