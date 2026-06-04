package io.github.sophon.discord.feat.config

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.flatMap
import io.github.sophon.core.domain.map
import io.github.sophon.core.domain.mapError
import io.github.sophon.core.domain.onSuccess
import io.github.sophon.core.feature.module.CoreFeatureRepo
import io.github.sophon.discord.feat.config.usecase.BindToDiscordFeaturesUseCase
import io.github.sophon.discord.feat.config.usecase.LoadConfigurationUseCase
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.toDomainError

internal class BotFeatureRepo(
    private val coreFeatureRepo: CoreFeatureRepo,
    private val loadConfigurationUseCase: LoadConfigurationUseCase,
    private val bindToDiscordFeaturesUseCase: BindToDiscordFeaturesUseCase,
) {
    private val featureList: MutableList<DiscordRegisteredFeature> = mutableListOf()

    fun initialize(): EmptyResult<BotError> {
        val result = loadConfigurationUseCase.invoke()
            .flatMap { config ->
                coreFeatureRepo.initialize(config)
                    .mapError { it.toDomainError() }
                    .flatMap {
                        bindToDiscordFeaturesUseCase.invoke()
                            .onSuccess { features ->
                                featureList.clear()
                                featureList.addAll(features)
                            }
                            .map { }
                    }
            }

        return result
    }

    fun getFeatures(): List<DiscordRegisteredFeature> {
        val result = featureList.toList()
        return result
    }


    private companion object {
        const val TAG = "FeatureRepo"
    }
}
