package io.github.sophon.wiki.app.util

import io.github.sophon.wiki.model.Move
import io.github.sophon.wiki.model.game.SF6MoveProperties

/**
 * The input comes from the move ID; the wiki's notation arrives as an alias and gets the same normalization,
 * so `214LP~6P` still finds `a.k.i._214lp_6p`.
 */
internal fun Move.normalizeSuperCombo(): Move {
    val normalizedInput = input.normalizeSuperComboInput()
    val normalizedAliases = aliases
        .flatMap { wikiInput -> formAliases(wikiInput) }
        .filterNot { alias -> alias == normalizedInput }
        .distinct()

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}

private fun String.normalizeSuperComboInput(): String {
    val normalized = cleanSuperComboInput()
        .normalize2dInputs()
        .replace("360+", "360")
    return normalized
}

/**
 * The motion alias comes from the wiki notation (`236HP` → `qcfhp`), so it's formed before normalization.
 */
private fun Move.formAliases(wikiInput: String): List<String> {
    val normalizedWikiInput = wikiInput.normalizeSuperComboInput()
    val motionAlias = when (type) {
        "super" -> formSuperLevel()
        else -> wikiInput.formMotionInput()
    }?.lowercase()

    val aliases = buildList {
        add(normalizedWikiInput)
        motionAlias?.let { add(it) }
        addAll(normalizedWikiInput.create2dAliases(isPartial = true))
    }
    return aliases
}

/**
 * The SF6 super level follows from its meter cost; critical arts (`(ca)` in the move ID) get none.
 */
private fun Move.formSuperLevel(): String? {
    if (remoteId.orEmpty().contains("(ca)", ignoreCase = true)) return null
    val superCost = (gameProperties as? SF6MoveProperties)?.superGainOnHit?.toIntOrNull() ?: return null

    val superLevel = when (superCost) {
        -10_000 -> "sa1"
        -20_000 -> "sa2"
        else -> "sa3"
    }
    return superLevel
}

private fun String.formMotionInput(): String? {
    val motion = when {
        startsWith("41236") -> replaceFirst("41236", "hcf")
        startsWith("63214") -> replaceFirst("63214", "hcb")
        startsWith("214") -> replace("214", "qcb")
        startsWith("236") -> replace("236", "qcf")
        startsWith("623") -> replaceFirst("623", "dp")
        startsWith("421") -> replaceFirst("421", "bdp")
        startsWith("360+") -> replaceFirst("360+", "spd")
        startsWith("360") -> replaceFirst("360", "spd")
        startsWith("2") -> replaceFirst("2", "cr")
        startsWith("5") -> replaceFirst("5", "st")
        else -> null
    }
    return motion
}

private fun String.cleanSuperComboInput(): String {
    var result = this.trim().lowercase().replace(" ", "")

    for ((old, new) in _root_ide_package_.io.github.sophon.wiki.app.util.superComboInputReplacementList) {
        result = result.replace(old, new)
    }

    if (result.startsWith("fnddf")) {
        result = result.replaceFirst("fnddf", "cd")
    }

    result = result
        .replace("rage.", "r.")
        .replace("heat.", "h.")

    //BAD.1+2 -> bad1+2
    result = result.split(".").let {
        if (it.first().length == 3) {
            result.replace(".", "")
        } else {
            result
        }
    }

    return result
}


private val superComboInputReplacementList = listOf(
    "," to "",
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
    "ss+" to "ss",
    "ss." to "ss",
    "*+" to "*",
    "ws." to "ws",
    "fc." to "fc",
    "bt." to "bt",
)
