package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.architecture.Result
import io.github.sophon.discord.feat.config.BotFeatureRepo
import io.github.sophon.discord.feat.core.domain.model.BotError

@ExcludeFromCoverage("plain client call")
internal class RefreshDataUseCase(
    private val featureRepo: Lazy<BotFeatureRepo>,
) {
    suspend operator fun invoke(): EmptyResult<BotError> {
        featureRepo.value.getFeatures().forEach { feature ->
            feature.refreshData()
        }

        return Result.Success(Unit)
    }
}
