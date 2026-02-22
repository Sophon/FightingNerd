package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.discord.domain.model.BotOutput

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