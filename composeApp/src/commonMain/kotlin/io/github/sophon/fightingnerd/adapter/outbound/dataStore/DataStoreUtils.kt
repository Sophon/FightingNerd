package io.github.sophon.fightingnerd.adapter.outbound.dataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

internal fun featureKey(featureName: String, gameId: String): Preferences.Key<Boolean> {
    return booleanPreferencesKey("${KEY_PREFIX_FEATURE}_${featureName}_${gameId}")
}


internal const val KEY_PREFIX_FEATURE = "settings_feature_"
