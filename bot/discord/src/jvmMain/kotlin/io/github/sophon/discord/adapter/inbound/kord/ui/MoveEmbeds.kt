package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.invisibleChar
import io.github.sophon.core.util.toColumns
import io.github.sophon.discord.app.model.discord.Emoji
import io.github.sophon.discord.app.model.response.BotResponse
import io.github.sophon.discord.app.model.response.ListResponse
import io.github.sophon.discord.app.model.response.MoveResponse

internal fun moveListEmbed(
    listResponse: ListResponse,
): EmbedBuilder.() -> Unit = {
    color = listResponse.dataSource.color?.let { Color(it) }

    if (listResponse.values.isEmpty()) {
        mandatoryField(
            name = listResponse.title,
            value = "Nothing found 😔",
        )
    } else {
        val numberedValues = listResponse.values
            .mapIndexed { index, value ->
                "${index + 1}. **$value**"
            }

        mandatoryField(
            name = listResponse.title,
            value = invisibleChar

            ,
            inline = false,
        )

        numberedValues
            .toColumns()
            .forEach { column ->
                val text = column.joinToString("\n")
                mandatoryField(
                    name = "",
                    value = text,
                )
            }
    }

    featureFooter(dataSource = listResponse.dataSource)
}

internal fun moveEmbed(
    move: MoveResponse,
): EmbedBuilder.() -> Unit {
    val embedBuilder = if (move.forceExpand) {
        expandedMoveEmbed(move)
    } else {
        coreMoveEmbed(move)
    }
    return embedBuilder
}

private fun coreMoveEmbed(
    move: MoveResponse
): EmbedBuilder.() -> Unit = {
    color = move.dataSource.color?.let { Color(it) }
    headerSection(move)

    primaryFieldsSection(fields = move.primaryFields)

    featureFooter(dataSource = move.dataSource)
}

private fun expandedMoveEmbed(
    move: MoveResponse,
): EmbedBuilder.() -> Unit = {
    color = move.dataSource.color?.let { Color(it) }
    headerSection(move)

    primaryFieldsSection(fields = move.primaryFields)
    detailsBulletPoints(move.secondaryFields)
    notesSection(move)
    embedImage(urls = move.hitboxImageList)

    featureFooter(dataSource = move.dataSource)
}

private fun EmbedBuilder.headerSection(move: MoveResponse) {
    title = move.input
    url = move.url
    description = when {
        move.characterName.isNotBlank() && move.moveName.isNullOrBlank().not() -> {
            "**${move.characterName}**: ${move.moveName}"
        }
        move.characterName.isNotBlank() -> {
            "**${move.characterName}**"
        }
        move.moveName.isNullOrBlank().not() -> {
            move.moveName
        }
        else -> "Move data"
    }

    move.characterImageUrl?.let { thumbnail { url = it } }
}

private fun EmbedBuilder.primaryFieldsSection(fields: List<BotResponse.Field>) {
    fields.forEach { field ->
        mandatoryField(name = field.title, value = field.value)
    }
}

private fun EmbedBuilder.notesSection(move: MoveResponse) {
    val aliasNote = if (move.aliasList.isNotEmpty()) {
        "**ALIAS**: ${move.aliasList.joinToString("; ")}"
    } else null

    val allNotes = buildList {
        addAll(move.noteList.map { it })
        aliasNote?.let { add(it) }
    }

    return optionalField(
        name = "",
        value = allNotes
            .emojify()
            .joinToString(separator = "") { note -> "* $note\n" },
        inline = false,
    )
}


private fun List<String>.emojify(): List<String> {
    return buildList {
        this@emojify.forEach { note ->
            val emojified = buildString {
                if (note.contains("Heat", ignoreCase = true)) append(Emoji.TK_HEAT)
                if (note.contains("Balcony Break", ignoreCase = true)) append(Emoji.TK_BALCONY)
                if (note.contains("Spike", ignoreCase = true)) append("⬇️ ")
                if (note.contains("Floor break", ignoreCase = true)) append(Emoji.TK_FLOOR)
                if (note.contains("Tornado", ignoreCase = true)) append(Emoji.TK_TORNADO)
                if (note.contains("Tailspin", ignoreCase = true)) append("️🌀 ")
                if (note.contains("Transition", ignoreCase = true)) append("️⏭️ ")
                if (note.contains("Homing", ignoreCase = true)) append(Emoji.TK_HOMING)
                if (note.contains("Throw", ignoreCase = true)) append(Emoji.THROW)
                if (note.contains("pc", ignoreCase = true)) append(Emoji.TK_PC)
                if (note.contains("weapon", ignoreCase = true)) append("⚔️ ")
                if (note.contains("jail", ignoreCase = true)) append("⛓️ ")
                if (note.contains("delay", ignoreCase = true)) append("⏳ ")
                if (note.contains("chip", ignoreCase = true)) append(Emoji.TK_CHIP)
                append(note)
            }
            add(emojified)
        }
    }
}
