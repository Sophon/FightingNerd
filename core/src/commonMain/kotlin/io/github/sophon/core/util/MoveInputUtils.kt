package io.github.sophon.core.util

fun String.normalize2dInputs(): String {
    var result = this
        .trim()
        .lowercase()
        .replace(" or ", "/")
        .replace(" ", "")
        .replace("j.", "j")
        .replace("f.", "f")

    val prefixTable = listOf(
        "(close)" to "c",
        "c." to "c",
    )

    for ((old, new) in prefixTable) {
        if (result.startsWith(old)) {
            result = new + result.removePrefix(old)
            break
        }
    }

    return result
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
    val splitParts = normalized.split(delimiter).map { it.trim() }

    val result = when {
        splitParts.isButtonVariants() -> splitParts.expandButtonVariants()
        else -> expandDirectionVariants(normalized, delimiter)
    }

    return result
}

private fun List<String>.isButtonVariants(): Boolean =
    drop(1).all { part -> part.all { it.isLetter() } }

private fun List<String>.expandButtonVariants(): List<String> {
    val sharedMotion = first().dropLastWhile { it.isLetter() }
    return map { part ->
        val button = part.takeLastWhile { it.isLetter() }
        "$sharedMotion$button".lowercase()
    }
}

private fun expandDirectionVariants(
    normalized: String,
    delimiter: String,
): List<String> {
    val prefix = normalized.takeWhile { it.isLetter() }
    val withoutPrefix = normalized.removePrefix(prefix)
    val directionParts = withoutPrefix.split(delimiter).map { it.trim() }
    val suffix = directionParts.last().dropWhile { it.isDigit() }
    return directionParts.map { part ->
        val direction = part.takeWhile { it.isDigit() }
        "$prefix$direction$suffix".lowercase()
    }
}

fun String.create2dAliases(
    isPartial: Boolean,
    delimiter: String = "/",
): List<String> {
    val orAliases = this.splitOr(isPartial, delimiter)

    val result = if (orAliases.isEmpty()) {
        this.add2dAliases()
    } else {
        buildList {
            addAll(orAliases)
            addAll(orAliases.flatMap { it.add2dAliases() })
        }.distinct()
    }

    return result
}

private fun String.add2dAliases(
    aliasList: List<String> = listOf(),
): List<String> {
    val result = when {
        startsWith("j") -> aliasList + listOf("j." + removePrefix("j"))
        startsWith("f") -> aliasList + listOf("f." + removePrefix("f"))
        startsWith("c") -> {
            buildList {
                addAll(aliasList)
                add("c." + removePrefix("c"))
                add("cl" + removePrefix("c"))
                add("cl." + removePrefix("c"))
            }
        }
        else -> aliasList
    }
    return result
}

fun String.chargeAlias(): List<String> {
    return if (contains("[4]")) {
        listOf(replace("[4]", "4"))
    } else emptyList()
}
