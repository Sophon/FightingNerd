package io.github.sophon.core.util

fun String.createAliasesFromSlash(): List<String> {
    val parts = split("/")
    if (parts.size < 2) return listOf()

    val motion = parts.first().dropLast(1)

    val aliases = parts.map { button ->
        "$motion${button.last().lowercase()}"
    }

    return aliases
}

fun String.normalize2dInputs(): String {
    var result = this
        .trim()
        .lowercase()
        .replace(" ", "")

    val replacementTable = listOf(
        "cl." to "c.",
        "cl" to "c.",
        "c" to "c.",
        "f" to "f.",
        "j" to "j.",
    )

    for ((old, new) in replacementTable) {
        if (result.startsWith(old)) {
            result = new + result.removePrefix(old)
            break
        }
    }

    return result
}

fun String.add2dAliases(aliasList: List<String> = listOf()): List<String> {
    return when {
        startsWith("j.") -> aliasList + listOf(replace("j.", "j"))
        startsWith("f.") -> aliasList + listOf(replace("f.", "f"))
        startsWith("c.") -> aliasList + listOf(replace("c.", "c"))
        else -> aliasList
    }
}