package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.component.ActionRowComponentBuilder
import dev.kord.rest.builder.component.ButtonBuilder
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import io.github.aakira.napier.Napier
import io.github.sophon.discord.EMBED_MAX_BUTTONS
import io.github.sophon.discord.EMBED_MAX_BUTTON_ACTION_LENGTH
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.DiscordButton
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class DiscordButtonBuilder {
    fun decodeToDomainModel(buttonId: String): DiscordButton? {
        val (key, value) = buttonId
            .split(":", limit = 2)
            .takeIf { it.size == 2 }
            ?: return null

        return when (key) {
            DiscordButton.KEY_QUERY -> DiscordButton.Query(value)
            DiscordButton.KEY_EDIT -> DiscordButton.Edit(value)
            DiscordButton.KEY_REDIRECT -> DiscordButton.Redirect(value)
            DiscordButton.KEY_TEXT -> DiscordButton.Text(value)
            else -> null
        }
    }

    fun createEmbedButtons(
        messageBuilder: MessageBuilder,
        buttonList: List<BotOutput.EmbedButton>,
        uuid: Uuid? = null,
    ) {
        val chunkSize = if (buttonList.size == 4) {
            2
        } else {
            5
        }

        buttonList
            .take(EMBED_MAX_BUTTONS)
            .chunked(chunkSize)
            .forEach { rowButtons ->
                val builtButtons = rowButtons.mapNotNull { button ->
                    createEmbedButton(button.action, button.label, uuid)
                }
                if (builtButtons.isEmpty()) return@forEach
                messageBuilder.actionRow {
                    builtButtons.forEach { components.add(it) }
                }
            }
    }


    private fun createEmbedButton(
        action: BotOutput.EmbedButton.Action,
        label: String,
        uuid: Uuid? = null,
    ): ActionRowComponentBuilder? {
        return when (action) {
            is BotOutput.EmbedButton.Action.Query -> {
                val customId = DiscordButton.Query(action.query).toString()
                interactionButtonOrNull(customId, label)
            }
            is BotOutput.EmbedButton.Action.Edit -> {
                uuid?.let {
                    val customId = DiscordButton.Edit(it.toString()).toString()
                    interactionButtonOrNull(customId, label)
                }
            }
            is BotOutput.EmbedButton.Action.Url -> {
                ButtonBuilder.LinkButtonBuilder(action.url)
                    .apply { this.label = label }
            }
            is BotOutput.EmbedButton.Action.Redirect -> {
                val customId = DiscordButton.Redirect(action.channelId).toString()
                interactionButtonOrNull(customId, label)
            }
            is BotOutput.EmbedButton.Action.Text -> {
                val customId = DiscordButton.Text(action.text).toString()
                interactionButtonOrNull(customId, label)
            }
        }
    }

    private fun interactionButtonOrNull(
        customId: String,
        label: String,
    ): ButtonBuilder.InteractionButtonBuilder? {
        val buttonBuilder = if (customId.length <= EMBED_MAX_BUTTON_ACTION_LENGTH) {
            ButtonBuilder.InteractionButtonBuilder(ButtonStyle.Primary, customId)
                .apply { this.label = label }
        } else {
            Napier.e(tag = TAG) { "custom_id exceeds Discord limit: ${customId.length}" }
            null
        }
        return buttonBuilder
    }


    private companion object {
        const val TAG = "DiscordButtonBuilder"
    }
}