package io.github.sophon.discord.featureRegistry.wikiWavu

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.domain.model.Move
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
                if (note.contains("Heat", ignoreCase = true)) append("<:fn_tk_heat:1487469502286532618> ")
                if (note.contains("Balcony Break", ignoreCase = true)) append("<:fn_tk_balcony:1487473524229804063> ")
                if (note.contains("Spike", ignoreCase = true)) append("⬇️ ")
                if (note.contains("Floor break", ignoreCase = true)) append("<:fn_tk_floor:1487469431327031328> ")
                if (note.contains("Tornado", ignoreCase = true)) append("<:fn_tk_tornado:1487469696923078726> ")
                if (note.contains("Tailspin", ignoreCase = true)) append("️🌀 ")
                if (note.contains("Transition", ignoreCase = true)) append("️⏭️ ")
                if (note.contains("Homing", ignoreCase = true)) append("️<:fn_tk_homing:1487469538776711390> ")
                if (note.contains("Throw", ignoreCase = true)) append("️<:fn_tk_throw:1487469629919199456> ")
                if (note.contains("pc", ignoreCase = true)) append("<:fn_tk_pc:1487469585556045885> ")
                if (note.contains("weapon", ignoreCase = true)) append("⚔️ ")
                if (note.contains("jail", ignoreCase = true)) append("⛓️ ")
                if (note.contains("delay", ignoreCase = true)) append("⏳ ")
                if (note.contains("chip", ignoreCase = true)) append("<:fn_tk_chip:1487469368945414275> ")
                append(note)
            }
            add(emojified)
        }
    }
}


private const val BLUE = 0x00095FB