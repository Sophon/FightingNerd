package io.github.sophon.discord.feat.config

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.discord.feat.config.usecase.BindToDiscordFeaturesUseCase
import io.github.sophon.discord.feat.config.usecase.LoadConfigurationUseCase
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.toDomainError

internal interface BotFeatureRepo {
    suspend fun initialize(): EmptyResult<BotError>
    fun getFeatures(): List<DiscordRegisteredFeature>
}

internal class BotFeatureRepoImpl(
    private val featureRepo: FeatureRepo,
    private val loadConfigurationUseCase: LoadConfigurationUseCase,
    private val bindToDiscordFeaturesUseCase: BindToDiscordFeaturesUseCase,
): BotFeatureRepo {
    private val featureList: MutableList<DiscordRegisteredFeature> = mutableListOf()

    override suspend fun initialize(): EmptyResult<BotError> {
        val result = loadConfigurationUseCase()
            .flatMap { config ->
                featureRepo.initialize(config)
                    .mapError { it.toDomainError() }
                    .flatMap {
                        bindToDiscordFeaturesUseCase(config)
                            .onSuccess { loadedFeatureList ->
                                this.featureList.apply {
                                    clear()
                                    addAll(loadedFeatureList)
                                    forEach {
                                        it.start()
                                    }
                                }
                            }
                            .map { }
                    }
            }

        return result
    }

    override fun getFeatures(): List<DiscordRegisteredFeature> {
        val result = featureList.toList()
        return result
    }


    private companion object {
        const val TAG = "FeatureRepo"
    }
}
