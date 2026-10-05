package io.github.sophon.wiki.app.util

import io.github.sophon.wiki.app.util.addExtraAliases
import io.github.sophon.wiki.app.util.create2dAliases
import io.github.sophon.wiki.model.Move

internal fun Move.normalizeXko(): Move {
    val normalizedInput = input.lowercase()
    val normalizedAliases = (aliases + normalizedInput.create2dAliases(isPartial = false).addExtraAliases(normalizedInput))

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}

/**
 * Also matches without the cancel mark (`~`) and without the parentheses around a button combination (`j.(m+h)` → `j.m+h`).
 */
private fun List<String>.addExtraAliases(input: String): List<String> {
    val aliases = buildList {
        addAll(this@addExtraAliases)

        if ("~" in input) {
            add(input.replace("~", ""))
        }

        if (input.contains("(") && "+" in input && input.endsWith(")")) {
            add(input.replace("(", "").dropLast(1))
        }
    }
    return aliases
}
