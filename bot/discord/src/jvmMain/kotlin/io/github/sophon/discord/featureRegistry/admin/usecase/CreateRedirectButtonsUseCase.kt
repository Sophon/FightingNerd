package io.github.sophon.discord.featureRegistry.admin.usecase

import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.DiscordRegisteredFeature

internal class CreateRedirectButtonsUseCase(
    private val featureList: List<DiscordRegisteredFeature>,
) {
    fun invoke(): BotOutput.ButtonSet {
        val buttonList = featureList
            .mapNotNull {
                val featureInfo = it.featureInfo
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