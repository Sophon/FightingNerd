package io.github.sophon.wiki.application.domain.util

import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.wiki.application.domain.model.gameProperties.SF6MoveProperties

internal fun Move.normalizeSuperCombo(): Move {
    val normalizedInput = input
        .cleanSuperComboInput()
        .normalize2dInputs()
        .replace("360+", "360")
    val normalizedAliases = (aliases + formAliases(normalizedInput))

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}

/**
 * The motion alias comes from the wiki notation (`236HP` → `qcfhp`), so it's formed from the input before normalization.
 */
private fun Move.formAliases(normalizedInput: String): List<String> {
    val motionAlias = when (type) {
        "super" -> formSuperLevel()
        else -> input.formMotionInput()
    }?.lowercase()

    val aliases = buildList {
        motionAlias?.let { add(it) }
        addAll(normalizedInput.create2dAliases(isPartial = true))
    }
    return aliases
}

/**
 * The SF6 super level follows from its meter cost; critical arts (`(ca)`) get none.
 */
private fun Move.formSuperLevel(): String? {
    if (id.contains("(ca)", ignoreCase = true)) return null
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

    for ((old, new) in superComboInputReplacementList) {
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
