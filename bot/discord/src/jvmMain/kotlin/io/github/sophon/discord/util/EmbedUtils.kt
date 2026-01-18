package io.github.sophon.discord.util

import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.actionRow
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.util.truncate
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.EMBED_MAX_BUTTONS
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_EDIT
import io.github.sophon.discord.usecase.CreateEmbedUseCase.Companion.KEY_QUERY
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal fun EmbedBuilder.mandatoryField(
    name: String,
    value: String?,
    inline: Boolean = true,
    escapeAsterisks: Boolean = false,
) {
    val formatted = value
        .orDash()
        .let { value ->
            if (escapeAsterisks) value.replace("*", "\\*")
            else value
        }
        .truncate(EMBED_MAX_LENGTH)

    field {
        this.name = name
        this.value = formatted
        this.inline = inline
    }
}

internal fun EmbedBuilder.optionalField(
    name: String,
    value: String?,
    inline: Boolean = true,
    escapeAsterisks: Boolean = false,
) {
    val formatted = value
        .orDash()
        .let { value ->
            if (escapeAsterisks) value.replace("*", "\\*")
            else value
        }
        .truncate(EMBED_MAX_LENGTH)

    if (value.isNullOrBlank().not()) {
        field {
            this.name = name
            this.value = formatted
            this.inline = inline
        }
    }
}

internal fun EmbedBuilder.optionalField(
    name: String,
    delimiter: String = "|",
    inline: Boolean = true,
    values: List<String?>,
) {
    if (values.all { it.isNullOrBlank() }) return

    val joinedValues = values
        .joinToString(" $delimiter ") { value ->
            value.takeUnless { it.isNullOrBlank() }.orDash()
        }

    field {
        this.name = name
        this.value = joinedValues.truncate(EMBED_MAX_LENGTH)
        this.inline = inline
    }
}

internal fun EmbedBuilder.separator() {
    field {
        name = "\u200B"
        value = ""
        inline = false
    }
}

internal fun EmbedBuilder.featureFooter(featureInfo: FeatureInfo) {
    footer {
        text = featureInfo.name
        icon = featureInfo.iconUrl
    }
}

internal fun List<Move>.toButtons(charName: String): List<BotOutput.EmbedButton> {
    return mapIndexed { index, move ->
        val query = "$charName ${move.input}"
        BotOutput.EmbedButton(
            label = (index + 1).toString(),
            action = BotOutput.EmbedButton.Action.Query(query),
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
internal fun MessageBuilder.createButtons(
    uuid: Uuid,
    buttons: List<BotOutput.EmbedButton>,
) {
    buttons
        .take(EMBED_MAX_BUTTONS)
        .chunked(5)
        .forEach { rowButtons ->
            actionRow {
                rowButtons.forEach { button ->
                    val action = when (button.action) {
                        is BotOutput.EmbedButton.Action.Query -> {
                            "$KEY_QUERY${button.action.query}"
                        }

                        is BotOutput.EmbedButton.Action.Edit -> "$KEY_EDIT$uuid"
                    }

                    interactionButton(
                        style = ButtonStyle.Primary,
                        customId = action,
                    ) {
                        label = button.label
                        disabled = false
                    }
                }
            }
        }
}
