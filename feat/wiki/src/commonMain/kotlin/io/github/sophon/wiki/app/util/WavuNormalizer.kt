package io.github.sophon.wiki.app.util


/**
 * Only strips the separators - `,`, `/` and the `+` after a direction or stance - the notation itself stays.
 */
internal fun String.minimizeTekkenMoveInput(keepSpaces: Boolean = false): String {
    var result = this.trim().lowercase()

    val separators = listOf(
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
        "ra+" to "ra",
        "*+" to "*",
    )

    if (keepSpaces.not()) {
        result = result.replace(" ", "")
    }

    for ((old, new) in separators) {
        result = result.replace(old, new)
    }

    return result
}

/**
 * Minimizes first, then rewrites the notation itself - `fff` becomes `wr`, `ddff` becomes `qcf` etc.
 */
internal fun String.normalizeTekkenMoveInput(keepSpaces: Boolean = false): String {
    var result = minimizeTekkenMoveInput(keepSpaces)

    val notations = listOf(
        "fff" to "wr",
        "ss+" to "ss",
        "ss." to "ss",
        "ws." to "ws",
        "fc." to "fc",
        "ddff" to "qcf",
    )

    for ((old, new) in notations) {
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

