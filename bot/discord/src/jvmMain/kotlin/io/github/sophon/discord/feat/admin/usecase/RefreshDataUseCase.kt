package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.domain.EmptyResult
import io.github.sophon.core.domain.Result
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.DiscordRegisteredFeature

internal class RefreshDataUseCase(
    private val featureList: Lazy<List<DiscordRegisteredFeature>>,
) {
    suspend fun invoke(): EmptyResult<BotError> {
        featureList.value.forEach { feature ->
            feature.refreshData()
        }

        return Result.Success(Unit)
    }
}
