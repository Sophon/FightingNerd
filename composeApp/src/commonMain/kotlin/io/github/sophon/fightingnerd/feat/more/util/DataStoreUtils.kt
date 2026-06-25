package io.github.sophon.fightingnerd.feat.more.util

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import io.github.sophon.fightingnerd.feat.more.KEY_PREFIX_FEATURE

internal fun featureKey(featureName: String, gameId: String): Preferences.Key<Boolean> {
    return booleanPreferencesKey("${KEY_PREFIX_FEATURE}_${featureName}_${gameId}")
}
