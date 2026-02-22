package io.github.sophon.discord.domain

import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.component.ActionRowComponentBuilder
import dev.kord.rest.builder.component.ButtonBuilder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal sealed class DiscordButton(
    private val key: String,
    private val value: String,
) {
    class Query(val query: String): DiscordButton(key = KEY_QUERY, value = query)

    class Edit(val messageId: String): DiscordButton(key = KEY_EDIT, value = messageId)

    class Redirect(val channelId: String): DiscordButton(key = KEY_REDIRECT, value = channelId)


    override fun toString(): String {
        return "$key:$value"
    }


    companion object {
        internal const val KEY_QUERY = "query"
        internal const val KEY_EDIT = "edit"
        internal const val KEY_REDIRECT = "redirect"

        fun createFromButtonId(buttonId: String): DiscordButton? {
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

        fun from(
            action: BotOutput.EmbedButton.Action,
            label: String,
            uuid: Uuid? = null,
        ): ActionRowComponentBuilder? {
            if (action is BotOutput.EmbedButton.Action.Edit && uuid == null) {
                val a = "here we are"
            }

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
}
