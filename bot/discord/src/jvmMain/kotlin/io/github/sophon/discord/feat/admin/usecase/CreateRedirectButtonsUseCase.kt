package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.discord.feat.core.domain.model.BotOutput

@ExcludeFromCoverage("UI")
internal class CreateRedirectButtonsUseCase {
    fun invoke(
        featureList: List<FeatureInfo>
    ): BotOutput.ButtonSet {
        val buttonList = featureList
            .mapNotNull { featureInfo ->
                featureInfo.feedbackDiscordChannelId?.let { channelId ->
                    BotOutput.EmbedButton(
                        label = featureInfo.name,
                        action = BotOutput.EmbedButton.Action.Redirect(channelId)
                    )
                }
            }

        return BotOutput.ButtonSet(buttonList)
    }
}