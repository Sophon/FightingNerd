package io.github.sophon.wiki.application.domain.util

import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.wiki.model.Move

internal fun Move.normalizeDreamCancel(): Move {
    val normalizedInput = input
        .normalize2dInputs()
        .lowercase()
    val normalizedAliases = (aliases + normalizedInput.create2dAliases(isPartial = true))

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}
