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

    val normalized = replace(" ", "")
    val splitParts = normalized.split(delimiter).map { it.trim() }

    val result = when {
        splitParts.isButtonVariants() -> splitParts.expandButtonVariants()
        isPartial.not() -> parts
        else -> expandDirectionVariants(normalized, delimiter)
    }

    return result
}

/**
 * partial: j5s1/j2s1
 * not partial: 46s/h~k
 */
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

fun String.chargeAlias(): List<String> {
    return if (contains("[4]")) {
        listOf(replace("[4]", "4"))
    } else emptyList()
}

fun String.isSpecial(): Boolean {
    if (startsWith("22")) return true
    val pattern = Regex("""\d{3}(BC|[A-Za-z])""") //contains 3 numbers followed by a char
    val result = pattern.containsMatchIn(this)
    return result
}


private fun List<String>.isButtonVariants(): Boolean {
    if (size < 2) return false
    if (first().lastOrNull()?.isLetter() != true) return false
    val restArePureButtons = drop(1).all { part ->
        val leadingLetters = part.takeWhile { it.isLetter() }
        val continuation = part.drop(leadingLetters.length)
        leadingLetters.length == 1 &&
                (continuation.isEmpty() || continuation.first().isLetterOrDigit().not())
    }
    return restArePureButtons
}

private fun List<String>.expandButtonVariants(): List<String> {
    val firstPart = first()
    val firstButton = firstPart.takeLastWhile { it.isLetter() }
    val motion = firstPart.dropLast(firstButton.length)

    val lastPart = last()
    val lastLeadingLetters = lastPart.takeWhile { it.isLetter() }
    val continuation = lastPart.drop(lastLeadingLetters.length)

    val expanded = mapIndexed { index, part ->
        val button = if (index == 0) firstButton else part.takeWhile { it.isLetter() }
        "$motion$button$continuation"
    }
    return expanded
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