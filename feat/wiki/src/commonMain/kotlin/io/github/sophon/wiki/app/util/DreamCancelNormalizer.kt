package io.github.sophon.wiki.app.util

import io.github.sophon.wiki.model.Move

/**
 * The input comes from the move ID; the wiki's notation arrives as an alias and gets the same normalization.
 */
internal fun Move.normalizeDreamCancel(): Move {
    val normalizedInput = input.normalizeDreamCancelInput()
    val notationList = (listOf(normalizedInput) + aliases.map { alias -> alias.normalizeDreamCancelInput() })
    val normalizedAliases = (notationList + notationList.flatMap { it.create2dAliases(isPartial = true) })
        .filterNot { alias -> alias == normalizedInput }
        .distinct()

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}

private fun String.normalizeDreamCancelInput(): String {
    val normalized = normalize2dInputs().lowercase()
    return normalized
}
