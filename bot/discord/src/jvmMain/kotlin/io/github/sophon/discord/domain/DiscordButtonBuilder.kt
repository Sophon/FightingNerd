package io.github.sophon.discord.domain

import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.component.ActionRowComponentBuilder
import dev.kord.rest.builder.component.ButtonBuilder
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import io.github.sophon.discord.EMBED_MAX_BUTTONS
import io.github.sophon.discord.domain.DiscordButton.Companion.KEY_EDIT
import io.github.sophon.discord.domain.DiscordButton.Companion.KEY_QUERY
import io.github.sophon.discord.domain.DiscordButton.Companion.KEY_REDIRECT
import io.github.sophon.discord.domain.DiscordButton.Edit
import io.github.sophon.discord.domain.DiscordButton.Query
import io.github.sophon.discord.domain.DiscordButton.Redirect
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
            KEY_QUERY -> Query(value)
            KEY_EDIT -> Edit(value)
            KEY_REDIRECT -> Redirect(value)
            else -> null
        }
    }

    fun createEmbedButtons(
        messageBuilder: MessageBuilder,
        buttonList: List<BotOutput.EmbedButton>,
        uuid: Uuid? = null,
    ) {
        buttonList
            .take(EMBED_MAX_BUTTONS)
            .chunked(5)
            .forEach { rowButtons ->
                messageBuilder.actionRow {
                    rowButtons.forEach { button ->
                        createEmbedButton(button.action, button.label, uuid)?.let {
                            components.add(it)
                        }
                    }
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
                val customId = Query(action.query).toString()
                ButtonBuilder.InteractionButtonBuilder(ButtonStyle.Primary, customId)
                    .apply { this.label = label }
            }
            is BotOutput.EmbedButton.Action.Edit -> {
                uuid?.let {
                    val customId = Edit(it.toString()).toString()
                    ButtonBuilder.InteractionButtonBuilder(ButtonStyle.Primary, customId)
                        .apply { this.label = label }
                }
            }
            is BotOutput.EmbedButton.Action.Url -> {
                ButtonBuilder.LinkButtonBuilder(action.url)
                    .apply { this.label = label }
            }
            is BotOutput.EmbedButton.Action.Redirect -> {
                val customId = Redirect(action.channelId).toString()
                ButtonBuilder.InteractionButtonBuilder(ButtonStyle.Primary, customId)
                    .apply { this.label = label }
            }
        }
    }
}