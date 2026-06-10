package io.github.sophon.fightingnerd.feat.more.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.fightingnerd.feat.more.SettingsError
import io.github.sophon.fightingnerd.feat.more.ui.featureSettings.FeatureSettingsState.UiFeatureSetting

internal class GetAvailableFeaturesUseCase(
    private val featureRepo: CoreFeatureRepo,
    private val store: DataStore<Preferences>,
) {
    fun invoke(): Result<List<UiFeatureSetting>, SettingsError> {
        val gameClients = featureRepo.getGameClients()
        val grouped = gameClients.entries.groupBy { it.value.getFeatureInfo().name }

        val featureList = grouped.map { (_, entries) ->
            val info = entries.first().value.getFeatureInfo()
            UiFeatureSetting(
                featureName = info.name,
                iconUrl = info.iconUrl.orEmpty(),
                version = info.version,
                gameList = entries.map { (game, _) ->
                    UiFeatureSetting.UiGame(
                        displayName = game.displayName,
                        isEnabled = true,
                    )
                },
            )
        }

        return Result.Success(featureList)
    }

//    private suspend fun updatePreferences(
//        featureList: List<FeatureInfo>
//    ): EmptyResult<SettingsError> {
//        return try {
//            store.edit { preferences ->
//                featureList.forEach { featureInfo ->
//                    val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
//                    if (key !in preferences) {
//                        preferences[key] = true
//                    }
//                }
//            }
//            Result.Success(Unit)
//        } catch (_: IOException) {
//            Result.Error(SettingsError.IO_ERROR)
//        } catch (_: Exception) {
//            Result.Error(SettingsError.UNKNOWN)
//        }
//    }
//
//    private suspend fun getFeatureSettings(
//        featureList: List<FeatureInfo>,
//    ): Result<List<SettingsState.FeatureSetting>, SettingsError> {
//        return try {
//            val preferences = store.data.first()
//            val featureSettings = featureList.map { featureInfo ->
//                val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
//                val isEnabled = preferences[key] ?: true
//                SettingsState.FeatureSetting(featureInfo, isEnabled)
//            }
//            Result.Success(featureSettings)
//        } catch (_: IOException) {
//            Result.Error(SettingsError.IO_ERROR)
//        } catch (_: Exception) {
//            Result.Error(SettingsError.UNKNOWN)
//        }
//    }
}