package io.github.sophon.wiki.application.domain.util

import io.github.sophon.core.util.chargeAlias
import io.github.sophon.wiki.application.domain.model.Move

/**
 * The input comes from the move ID; the wiki's notation arrives as an alias and gets the same normalization.
 */
internal fun Move.normalizeMizuumi(): Move {
    val normalizedInput = input.normalize2dInputs()
    val notationList = (listOf(normalizedInput) + aliases.map { alias -> alias.normalize2dInputs() })
    val derivedAliasList = notationList.flatMap { notation ->
        val derived = (notation.create2dAliases(isPartial = true) + notation.chargeAlias())
        derived
    }
    val normalizedAliases = (notationList + derivedAliasList)
        .filterNot { alias -> alias == normalizedInput }
        .distinct()

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}
