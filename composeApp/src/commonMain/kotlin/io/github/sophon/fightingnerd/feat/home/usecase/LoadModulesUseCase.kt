package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.wiki.domain.WikiClient
import io.github.sophon.fightingnerd.feat.home.model.HomeError
import io.github.sophon.fightingnerd.feat.module.ModuleRepo

internal class LoadModulesUseCase(
    private val store: DataStore<Preferences>,
    private val moduleRepo: ModuleRepo,
) {
    fun invoke(): Result<List<WikiClient>, HomeError> {
//        return store.data
//            .map { filterEnabledFeatures(it) }
//            .catch { e ->
//                emit(
//                    when (e) {
//                        is IOException -> Result.Error(HomeError.IO_ERROR)
//                        else -> Result.Error(HomeError.UNKNOWN)
//                    }
//                )
//            }

        val modules = moduleRepo.getGameClients().also {
            Napier.d(tag = TAG) { "Modules loaded: ${it.size}" }
        }
        return Result.Success(modules)
    }

//    private fun filterEnabledFeatures(
//        preferences: Preferences
//    ): Result<List<Module>, HomeError> {
//        val featureList = getAvailableFeatures()
//        val enabledFeatures = featureList.mapNotNull { featureInfo ->
//            val key = booleanPreferencesKey(KEY_PREFIX_FEATURE + featureInfo.name)
//            val isEnabled = preferences[key] ?: true
//            if (isEnabled) {
//                registry.getFeature(featureInfo)
//            } else {
//                null
//            }
//        }
//
//        return Result.Success(enabledFeatures)
//    }
//
//    private fun getAvailableFeatures(): List<FeatureInfo> {
//        return registry.getFeatures().map { it.featureInfo }
//    }


    private companion object {
        const val TAG = "GetAvailableFeaturesUseCase"
    }
}