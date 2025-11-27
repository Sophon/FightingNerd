package io.github.sophon.wikiwavu.util

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
        "ss+" to "ss",
        "ss." to "ss",
        "*+" to "*",
        "ws." to "ws",
        "fc." to "fc",
        "bt." to "bt",
    )

    if (keepSpaces.not()) {
        result = result.replace(" ", "")
    }

    for ((old, new) in motionInputs) {
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
