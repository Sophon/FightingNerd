package io.github.sophon.wikiwavu.integration

fun String.cleanMoveInput(keepSpaces: Boolean = false): String {
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
