package io.github.sophon.wiki.application.domain.util

import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.wiki.model.Move

/**
 * The id is the character's id and the normalized input.
 */
internal fun Move.normalizeXko(): Move {
    val normalizedInput = input.lowercase()
    val normalizedAliases = (aliases + normalizedInput.create2dAliases(isPartial = false).addExtraAliases(normalizedInput))

    val normalized = copy(
        id = "${characterId}_$normalizedInput",
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
