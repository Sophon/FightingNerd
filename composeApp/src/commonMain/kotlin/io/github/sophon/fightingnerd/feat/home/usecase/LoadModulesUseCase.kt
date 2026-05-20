package io.github.sophon.fightingnerd.feat.home.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.fightingnerd.core.model.Module
import io.github.sophon.fightingnerd.feat.config.ModuleRegistry
import io.github.sophon.fightingnerd.feat.home.model.HomeError

internal class LoadModulesUseCase(
    private val store: DataStore<Preferences>,
    private val moduleRegistry: ModuleRegistry,
) {
    fun invoke(): Result<List<Module>, HomeError> {
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

        val modules = moduleRegistry.getEnabledModules().also {
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