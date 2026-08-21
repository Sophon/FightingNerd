package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.Color
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.discord.URL_APP_STORE
import io.github.sophon.discord.URL_BUY_ME_COFFEE
import io.github.sophon.discord.URL_KOFI
import io.github.sophon.discord.URL_PLAY_STORE
import io.github.sophon.discord.feat.core.domain.DiscordButtonBuilder
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.util.mandatoryField
import kotlin.uuid.ExperimentalUuidApi

@ExcludeFromCoverage("UI")
@OptIn(ExperimentalUuidApi::class)
internal class CreatePromoEmbedUseCase(
    private val discordButtonBuilder: DiscordButtonBuilder,
) {
    suspend fun invoke(something: MessageChannelBehavior) {
        val botOutput = promoMessage()
        if (botOutput.primaryEmbedBuilder == null) return
        if (botOutput.buttons == null) return

        something.createMessage {
            embed(botOutput.primaryEmbedBuilder)

            discordButtonBuilder.createEmbedButtons(
                messageBuilder = this,
                buttonList = botOutput.buttons.buttonList,
            )
        }
    }

    private fun promoMessage(): BotOutput {
        val embed: EmbedBuilder.() -> Unit = {
            title = "ENJOY THE BOT?"
            color = Color(PURPLE)

            mandatoryField(
                name = "",
                value = "Buy me a coffee.\n" +
                        "The project is also available on mobile.",
            )
        }

        val buttons = BotOutput.ButtonSet(
            buttonList = listOf(
                BotOutput.EmbedButton(
                    label = "☕️ KO-FI",
                    action = BotOutput.EmbedButton.Action.Url(URL_KOFI),
                ),
                BotOutput.EmbedButton(
                    label = "☕️ BUY-ME-COFFEE",
                    action = BotOutput.EmbedButton.Action.Url(URL_BUY_ME_COFFEE),
                ),
                BotOutput.EmbedButton(
                    label = "🍏 iPhone",
                    action = BotOutput.EmbedButton.Action.Url(URL_APP_STORE)
                ),
                BotOutput.EmbedButton(
                    label = "🤖 Android",
                    action = BotOutput.EmbedButton.Action.Url(URL_PLAY_STORE)
                ),
            )
        )

        val output = BotOutput(
            primaryEmbedBuilder = embed,
            buttons = buttons,
        )

        return output
    }


    private companion object {
        const val PURPLE = 0x00A020F0
    }
}