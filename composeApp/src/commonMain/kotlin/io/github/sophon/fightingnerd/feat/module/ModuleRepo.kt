package io.github.sophon.fightingnerd.feat.module

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.Config
import io.github.sophon.fightingnerd.core.model.AppError
import io.github.sophon.fightingnerd.core.model.Module
import io.github.sophon.fightingnerd.feat.module.usecase.LoadConfigUseCase

internal class ModuleRepo(
    private val loadConfigUseCase: LoadConfigUseCase,
    private val availableModules: List<Module>,
) {
    private var enabledModules: List<Module> = emptyList()

    fun getEnabledModules(): List<Module> {
        return enabledModules
    }

    suspend fun initialize(): EmptyResult<AppError> {
        val result = loadConfigUseCase.invoke()
            .map { config ->
                enabledModules = resolveModules(config)
                enabledModules.forEach { it.onInit() }
            }

        return result
    }

    private fun resolveModules(config: Config): List<Module> {
        val moduleByName = availableModules.associateBy { it.featureInfo.name }
        return config.featureList
            .filter { it.isEnabled }
            .mapNotNull { featureConfig ->
                val module = moduleByName[featureConfig.name]
                if (module == null) {
                    Napier.w(tag = TAG) { "No Module impl found for: ${featureConfig.name}" }
                    return@mapNotNull null
                }
                module.registerGames(featureConfig.supportedGameList)
                module
            }
    }

    private companion object {
        const val TAG = "ModuleRegistry"
    }
}