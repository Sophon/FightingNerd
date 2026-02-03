package io.github.sophon.data

import io.github.sophon.ewgf.data.Player


internal fun Player?.toDomain(): io.github.sophon.domain.Player? {
    if (this == null) return null

    return io.github.sophon.domain.Player(
        discordId = discordId,
        polarisId = polarisId,
    )
}