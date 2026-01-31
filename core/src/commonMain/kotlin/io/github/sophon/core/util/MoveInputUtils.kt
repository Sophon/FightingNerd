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
    )

    for ((old, new) in replacementTable) {
        result = result.replace(old, new)
    }

    return result
}

fun List<String>.add2dAliases(input: String): List<String> {
    return when {
        input.startsWith("j.") -> this + listOf(input.replace("j.", "j"))
        input.startsWith("f.") -> this + listOf(input.replace("f.", "f"))
        input.startsWith("c.") -> this + listOf(input.replace("c.", "c"))
        else -> this
    }
}