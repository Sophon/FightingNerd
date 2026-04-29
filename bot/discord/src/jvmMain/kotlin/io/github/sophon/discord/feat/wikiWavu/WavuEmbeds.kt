package io.github.sophon.discord.feat.wikiWavu

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.domain.model.Emoji
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal fun wavuMoveEmbed(
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = move.input
    url = move.urls.wikiUrl
    description = if (move.name.isNullOrBlank()) {
        "**${move.charName}**"
    } else {
        "**${move.charName}**: ${move.name.orEmpty()}"
    }
    color = Color(BLUE)

    move.urls.characterImage?.let { thumbnail { url = it } }

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Hit", value = move.onHit)
    mandatoryField(name = "Block", value = move.onBlock)
    mandatoryField(name = "CH", value = (move.onCH ?: move.onHit))
    mandatoryField(name = "Level", value = move.guard)


    optionalField(name = "Recovery", value = move.recovery)
    optionalField(name = "Damage", value = move.damage)

    createNotes(move)

    move.urls.videoId?.let { url ->
        optionalField(name = "Video", value = "[Link](${url})", inline = false)
    }

    featureFooter(featureInfo)
}


private fun EmbedBuilder.createNotes(move: Move) {
    val aliasNote = if (move.aliases.isNotEmpty()) {
        "Alt inputs: ${move.aliases.joinToString("; ")}"
    } else null

    val allNotes = buildList {
        addAll(move.notes.map { it })
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


private const val BLUE = 0x00095FB