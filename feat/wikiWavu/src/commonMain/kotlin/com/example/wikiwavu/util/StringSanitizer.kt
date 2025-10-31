package com.example.wikiwavu.util

fun String.cleanMoveInput(): String {
    var result = this.trim().lowercase()

    val motionInputs = listOf(
        " " to "",
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

    for ((old, new) in motionInputs) {
        result = result.replace(old, new)
    }


    if (result.startsWith("fnddf")) {
        result = result.replaceFirst("fnddf", "cd")
    }

    result = result
        .replace("rage.", "r.")
        .replace("heat.", "h.")

    return result
}
