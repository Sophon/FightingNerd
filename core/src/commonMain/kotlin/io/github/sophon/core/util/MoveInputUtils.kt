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

fun String.normalize2dInputs(): String {
    var result = this
        .trim()
        .lowercase()
        .replace(" or ", "/")
        .replace(" ", "")

    val replacementTable = mutableListOf(
        "(close)" to "c",
        "f." to "f",
        "j." to "j",
    )

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
        startsWith("c.") -> {
            aliasList + listOf(replace("c.", "c")) + listOf(replace("c.", "cl"))
        }
        else -> aliasList
    }
}

fun String.splitOr(
    isPartial: Boolean,
    delimiter: String = "/",
): List<String> {
    if (contains(delimiter).not()) return emptyList()

    val parts = split(delimiter).map { it.trim() }
    if (parts.size < 2) return emptyList()

    if (isPartial.not()) return parts

    val normalized = replace(" ", "")
    val prefix = normalized.takeWhile { it.isLetter() }
    val withoutPrefix = normalized.removePrefix(prefix)
    val directions = withoutPrefix.split(delimiter).map { it.trim() }
    val suffix = directions.last().dropWhile { it.isDigit() }

    return directions.map { part ->
        val direction = part.takeWhile { it.isDigit() }
        "$prefix$direction$suffix".lowercase()
    }
}

fun String.create2dAliases(
    isPartial: Boolean,
    delimiter: String = "/",
): List<String> {
    val alias2d = this.add2dAliases()
    val orAliases = this.splitOr(isPartial, delimiter)

    val result = buildList {
        addAll(alias2d)
        addAll(orAliases)
    }.distinct()

    return result
}
