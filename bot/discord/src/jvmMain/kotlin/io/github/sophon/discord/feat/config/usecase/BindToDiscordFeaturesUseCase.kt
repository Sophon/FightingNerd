package io.github.sophon.discord.feat.config.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.CoreFeatureRepo
import io.github.sophon.core.featureConfig.model.Config
import io.github.sophon.core.wiki.model.WikiClient
import io.github.sophon.discord.feat.admin.AdminDiscordFeature
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.feat.core.domain.model.GameWikiDiscordFeature
import kotlin.collections.filterKeys

internal class BindToDiscordFeaturesUseCase(
    private val availableFeatures: List<DiscordRegisteredFeature>,
    private val coreFeatureRepo: CoreFeatureRepo,
    private val adminFeature: AdminDiscordFeature,
) {
    fun invoke(config: Config): Result<List<DiscordRegisteredFeature>, BotError> {
        val gameClients: Map<Game, WikiClient> = coreFeatureRepo.getGameClients()

        availableFeatures.forEach { feature ->
            if (feature is GameWikiDiscordFeature) {
                val supportedGames = feature.featureInfo.supportedGameSet
                val relevantClients = gameClients.filterKeys { it in supportedGames }
                feature.registerWikiClients(relevantClients)
            }
        }

        val orderedFeatures = sortByConfigOrder(availableFeatures, config)
        val result = orderedFeatures + adminFeature
        return Result.Success(result)
    }

    private fun sortByConfigOrder(
        features: List<DiscordRegisteredFeature>,
        config: Config,
    ): List<DiscordRegisteredFeature> {
        val indexByName = config.featureList
            .withIndex()
            .associate { (index, feature) -> feature.name to index }

        val sorted = features.sortedBy { feature ->
            indexByName[feature.featureInfo.name] ?: Int.MAX_VALUE
        }
        return sorted
    }
}
