package io.github.sophon.wiki.app.util

/**
 * The close prefix becomes `cl`, not `c` - close D (`c.D` → `cld`) and Blowback (`CD` → `cd`) are different moves.
 */
internal fun String.normalize2dInputs(): String {
    var result = this
        .trim()
        .lowercase()
        .replace(" or ", "/")
        .replace(" ", "")
        .replace("j.", "j")
        .replace("f.", "f")

    for ((old, new) in _root_ide_package_.io.github.sophon.wiki.app.util.closePrefixList) {
        if (result.startsWith(old)) {
            result = new + result.removePrefix(old)
            break
        }
    }

    return result
}

/**
 * partial: j5s1/j2s1
 * not partial: 46s/h~k
 */
internal fun String.create2dAliases(
    isPartial: Boolean,
    delimiter: String = "/",
): List<String> {
    val orAliases = splitOr(isPartial, delimiter)

    val result = if (orAliases.isEmpty()) {
        add2dAliases()
    } else {
        (orAliases + orAliases.flatMap { it.add2dAliases() }).distinct()
    }
    return result
}

private fun String.splitOr(
    isPartial: Boolean,
    delimiter: String,
): List<String> {
    if (contains(delimiter).not()) return emptyList()

    val parts = split(delimiter).map { it.trim() }
    if (parts.size < 2) return emptyList()

    val normalized = replace(" ", "")
    val splitParts = normalized.split(delimiter).map { it.trim() }

    val result = when {
        splitParts.isButtonVariants() -> splitParts.expandButtonVariants()
        isPartial.not() -> parts
        else -> _root_ide_package_.io.github.sophon.wiki.app.util.expandDirectionVariants(normalized, delimiter)
    }
    return result
}

private fun List<String>.isButtonVariants(): Boolean {
    if (size < 2) return false
    if (first().lastOrNull()?.isLetter() != true) return false
    val restArePureButtons = drop(1).all { part ->
        val leadingLetters = part.takeWhile { it.isLetter() }
        val continuation = part.drop(leadingLetters.length)
        (leadingLetters.length == 1) && (continuation.isEmpty() || continuation.first().isLetterOrDigit().not())
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
    val expanded = directionParts.map { part ->
        val direction = part.takeWhile { it.isDigit() }
        "$prefix$direction$suffix".lowercase()
    }
    return expanded
}

/**
 * `cl` is the close prefix - a close move is also found by `cl.`, `c.` and `c`; the input of the move that owns `c…`
 * (Blowback `cd` vs close D `cld`) wins over the alias.
 */
private fun String.add2dAliases(): List<String> {
    val aliases = when {
        startsWith("j") -> listOf("j." + removePrefix("j"))
        startsWith("f") -> listOf("f." + removePrefix("f"))
        startsWith(_root_ide_package_.io.github.sophon.wiki.app.util.CLOSE_PREFIX) -> {
            val withoutPrefix = removePrefix(_root_ide_package_.io.github.sophon.wiki.app.util.CLOSE_PREFIX)
            listOf("cl.$withoutPrefix", "c.$withoutPrefix", "c$withoutPrefix")
        }

        else -> emptyList()
    }
    return aliases
}


private const val CLOSE_PREFIX = "cl"

private val closePrefixList = listOf(
    "(close)" to _root_ide_package_.io.github.sophon.wiki.app.util.CLOSE_PREFIX,
    "c." to _root_ide_package_.io.github.sophon.wiki.app.util.CLOSE_PREFIX,
)
