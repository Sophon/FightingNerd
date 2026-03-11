package io.github.sophon.data.local

import io.github.sophon.ewgf.data.Player


internal fun Player?.toDomain(): io.github.sophon.domain.model.Player? {
    if (this == null) return null

    return io.github.sophon.domain.model.Player(
        discordId = discordId,
        polarisId = polarisId,
    )
}