package io.github.sophon.wiki.app.util

import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.T8Properties

internal fun Move.normalizeT8(): Move {
    val normalizedInput = input.cleanMoveInput()
    val normalizedAliases = normalizedInput.formAliases(aliases)

    val mappedProperties = ((gameProperties as? T8Properties) ?: T8Properties())
    val properties = mappedProperties.copy(
        isHeat = _root_ide_package_.io.github.sophon.wiki.app.util.isHeat(
            notes = notes,
            input = normalizedInput,
            aliases = normalizedAliases
        ),
        stance = (normalizedInput.getStance() ?: normalizedAliases.firstNotNullOfOrNull { it.getStance() }), //TODO: this has flawed impl - what if there are multiple stances in the aliases?
    )

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
        gameProperties = properties,
    )
    return normalized
}

internal fun String.cleanMoveInput(keepSpaces: Boolean = false): String {
    var result = this.trim().lowercase()

    val motionInputs = listOf(
        "," to "",
        "/" to "",
        "d+" to "d",
        "f+" to "f",
        "u+" to "u",
        "b+" to "b",
        "n+" to "n",
        "ws+" to "ws",
        "fc+" to "fc",
        "cd+" to "cd",
        "wr+" to "wr",
        "fff" to "wr",
        "ra+" to "ra",
        "ss+" to "ss.",
        "*+" to "*",
        "ws." to "ws",
        "fc." to "fc",
        "bt." to "bt",
        "ddff" to "qcf",
    )

    if (keepSpaces.not()) {
        result = result.replace(" ", "")
    }

    for ((old, new) in motionInputs) {
        result = result.replace(old, new)
    }


    when {
        result.contains("fnddf#") -> result = result.replace("fnddf#", "cd#")
        result.contains("fnddf") -> result = result.replace("fnddf", "cd.")
    }

    result = result
        .replace("rage.", "r.")
        .replace("heat.", "h.")

    return result
}

/**
 * Receiver is the normalized input - its own variants become aliases too, but the input itself is dropped.
 */
private fun String.formAliases(aliasList: List<String>): List<String> {
    val expanded = (aliasList + this)
        .map { it.cleanMoveInput(keepSpaces = true) }
        .filter { it.isNotEmpty() }
        .flatMap { it.expandVariants() }

    val result = expanded
        .distinct()
        .filterNot { it == this }

    return result
}

private fun String.expandVariants(): List<String> {
    val variants = mutableListOf(this)

    when {
        startsWith("cd.df#") -> variants.add(replaceFirst("cd.df#", "cd#"))
        startsWith("cd.df") -> {
            variants.add(replaceFirst("cd.df", "cd"))
            variants.add(replaceFirst("cd.df", "cd."))
        }
        startsWith("cd.") -> variants.add(replace("cd.", "cd"))
    }

    if (contains(".") && split(".").first().length == 3) {
        variants.add(replace(".", ""))
    }

    if (startsWith("ss.")) {
        variants.add(replace("ss.", "ss"))
    }

    if (contains("h.", ignoreCase = true) && startsWith("h.", ignoreCase = true).not()) {
        val heatless = replace("h.", "")
        variants.add("h.$heatless")
    }

    if (this == "h.2+3") {
        variants.addAll(listOf("hs", "heatsmash"))
    }

    if (contains("cd.", ignoreCase = true)) {
        variants.add(replace("cd.", "cd"))
    }

    if (startsWith("hfc", ignoreCase = true)) {
        variants.add(replace("hfc", "fc"))
    }

    return variants
}

private fun isHeat(
    notes: List<String>,
    input: String,
    aliases: List<String>,
): Boolean {
    val isHeat = (notes.any { it.contains("Heat Engager", ignoreCase = true) }
            || notes.any { it.contains("Heat Smash", ignoreCase = true) }
            || input.contains("H.", ignoreCase = true)
            || aliases.any { it.contains("H.", ignoreCase = true) })

    return isHeat
}

private fun String.getStance(): String? {
    when {
        startsWith("BT", ignoreCase = true) -> return "BT"
        startsWith("CD", ignoreCase = true) -> return "CD"
    }

    val stance = take(3).takeIf {
        (length >= 4 && it.all { char -> char.isLetter() } && get(3) == '.' && it != "otg")
    }
    return stance?.uppercase()
}
