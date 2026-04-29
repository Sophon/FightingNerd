package io.github.sophon.discord.feat.admin.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.feat.core.domain.model.BotOutput

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