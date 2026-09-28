package io.github.sophon.discord.feat.config

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.core.featureConfig.FeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.discord.feat.config.usecase.BindToDiscordFeaturesUseCase
import io.github.sophon.discord.feat.config.usecase.LoadConfigurationUseCase
import io.github.sophon.discord.feat.core.domain.Scheduler
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.toDomainError
import io.github.sophon.wiki.application.domain.model.RefreshEvent
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.domain.model.WikiError
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn

internal interface BotFeatureRepo {
    suspend fun initialize(): EmptyResult<BotError>
    fun getFeatures(): List<DiscordRegisteredFeature>
}

internal class BotFeatureRepoImpl(
    private val featureRepo: FeatureRepo,
    private val loadConfigurationUseCase: LoadConfigurationUseCase,
    private val bindToDiscordFeaturesUseCase: BindToDiscordFeaturesUseCase,

//    //region HEX migration
//    private val configureWikiUseCase: ConfigureWikiUseCase,
//    private val refreshWikiDataUseCase: RefreshDataUseCase,
//    private val scheduler: Scheduler,
//    private val scope: CoroutineScope,
//    //endregion
): BotFeatureRepo {
    private val featureList: MutableList<DiscordRegisteredFeature> = mutableListOf()

    override suspend fun initialize(): EmptyResult<BotError> {
        val result = loadConfigurationUseCase.invoke()
            .flatMap { config ->
//                //region HEX migration
//                bindHexagonalWiki(config)
//                //endregion

                featureRepo.initialize(config)
                    .mapError { it.toDomainError() }
                    .flatMap {
                        bindToDiscordFeaturesUseCase.invoke(config)
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

                        //region HEX migration
//                        Result.Success(Unit)
                        //endregion
                    }
            }

        return result
    }

    override fun getFeatures(): List<DiscordRegisteredFeature> {
        val result = featureList.toList()
        return result
    }


//    //region HEX migration
//    private suspend fun bindHexagonalWiki(config: Config) {
//        config.toWikiConfig()
//            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
//            .onSuccess { scheduleHexagonalWikiRefresh() }
//            .onError { error ->
//                Napier.e(tag = TAG) { "Hexagonal wiki configuration failed: $error" }
//            }
//    }
//
//    private fun scheduleHexagonalWikiRefresh() {
//        scheduler.start {
//            refreshWikiDataUseCase().collect { event ->
//                when (event) {
//                    is RefreshEvent.Failed -> Napier.e(tag = TAG) { "Hexagonal wiki refresh failed: ${event.error}" }
//                    is RefreshEvent.Finished -> Napier.i(tag = TAG) { "Hexagonal wiki refresh finished: ${event.successCount} characters" }
//                }
//            }
//        }
//            .launchIn(scope)
//    }
//
//    /**
//     * Disabled features are dropped, same as the legacy [FeatureRepo] - everything available is enabled.
//     */
//    private fun Config.toWikiConfig(): Result<WikiConfig, WikiError> {
//        val gameSet = featureList
//            .filter { it.isEnabled }
//            .flatMap { it.supportedGameList }
//            .toSet()
//
//        val result = WikiConfig.create(
//            availableGameSet = gameSet,
//            enabledGameSet = gameSet,
//        )
//        return result
//    }
//    //endregion


    private companion object {
        const val TAG = "FeatureRepo"
    }
}
