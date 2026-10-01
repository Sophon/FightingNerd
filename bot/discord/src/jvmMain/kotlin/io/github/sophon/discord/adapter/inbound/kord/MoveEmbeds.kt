package io.github.sophon.discord.adapter.inbound.kord

import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.discord.app.domain.model.BotResponse
import io.github.sophon.discord.feat.core.domain.model.Emoji

internal fun genericMoveEmbed(
    move: BotResponse.MoveResponse
): EmbedBuilder.() -> Unit = {
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

    move.primaryFields.forEach { field ->
        mandatoryField(name = field.title, value = field.value)
    }

    if (move.isCollapsedByDefault.not()) {
        move.secondaryFields.forEach { field ->
            optionalField(name = field.title, value = field.value)
        }
    }

    notesSection(move)


    featureFooter(dataSource = move.dataSource)
}


private fun EmbedBuilder.notesSection(move: BotResponse.MoveResponse) {
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

