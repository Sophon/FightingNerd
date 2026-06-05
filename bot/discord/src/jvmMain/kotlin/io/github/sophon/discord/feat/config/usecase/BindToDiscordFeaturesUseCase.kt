package io.github.sophon.discord.feat.config.usecase

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.featureConfig.model.Game
import io.github.sophon.core.featureConfig.CoreFeatureRepo
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
    fun invoke(): Result<List<DiscordRegisteredFeature>, BotError> {
        val gameClients: Map<Game, WikiClient> = coreFeatureRepo.getGameClients()

        availableFeatures.forEach { feature ->
            if (feature is GameWikiDiscordFeature) {
                val supportedGames = feature.featureInfo.supportedGameSet
                val relevantClients = gameClients.filterKeys { it in supportedGames }
                feature.registerWikiClients(relevantClients)
            }
        }

        val result = availableFeatures + adminFeature
        return Result.Success(result)
    }
}
