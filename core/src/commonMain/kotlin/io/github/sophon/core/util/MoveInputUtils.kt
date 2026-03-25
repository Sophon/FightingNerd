package io.github.sophon.core.util

fun String.createAliasesFromSlash(
    isPartial: Boolean,
): List<String> {
    val parts = split("/")
    if (parts.size < 2) return listOf()

    val aliases = if (isPartial) {
        parts.map { button ->
            val motion = parts.first().dropLast(1)
            "$motion${button.last().lowercase()}"
        }
    } else {
        parts
    }.map { it.trim() }

    return aliases
}

fun String.normalize2dInputs(minimizeClose: Boolean = true): String {
    var result = this
        .trim()
        .lowercase()
        .replace(" or ", "/")
        .replace(" ", "")

    val replacementTable = mutableListOf(
        "(close)" to "c.",
        "f" to "f.",
        "j" to "j.",
    )

    if (minimizeClose) {
        replacementTable.addAll(
            listOf(
                "cl." to "c.",
                "cl" to "c.",
                "c" to "c.",
            )
        )
    }

    for ((old, new) in replacementTable) {
        if (result.startsWith(old) && result.startsWith(new).not()) {
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

fun String.useForwardVariantOnly(): String {
    return this
        .replace(" ", "")
        .replace("4/6", "6")
}