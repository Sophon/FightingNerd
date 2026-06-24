package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.fightingnerd.feat.more.model.SettingsError
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState.UiFeatureSetting
import io.github.sophon.fightingnerd.feat.more.util.featureKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class SaveFeatureConfigUseCase(
    private val store: DataStore<Preferences>,
) {
    suspend fun invoke(
        featureList: List<UiFeatureSetting>,
    ): EmptyResult<SettingsError> {
        return withContext(Dispatchers.IO) {
            try {
                store.edit { prefs ->
                    featureList.forEach { feature ->
                        feature.gameList.forEach { game ->
                            prefs[featureKey(feature.featureName, game.id)] = game.isEnabled
                        }
                    }
                }
                Result.Success(Unit)
            } catch (_: IOException) {
                Result.Error(SettingsError.IO_ERROR)
            }
        }
    }
}
