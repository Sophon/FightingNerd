package io.github.sophon.wiki.application.domain.util

import io.github.sophon.core.util.chargeAlias
import io.github.sophon.core.util.create2dAliases
import io.github.sophon.core.util.normalize2dInputs
import io.github.sophon.core.wiki.model.Move

internal fun Move.normalizeMizuumi(): Move {
    val normalizedInput = input.normalize2dInputs()
    val normalizedAliases = (aliases + normalizedInput.create2dAliases(isPartial = true) + normalizedInput.chargeAlias())

    val normalized = copy(
        input = normalizedInput,
        aliases = normalizedAliases,
    )
    return normalized
}
