package io.github.sophon.data

import io.github.sophon.admin.data.Ban
import kotlinx.datetime.Instant

internal fun Ban?.toDomain(): io.github.sophon.domain.model.Ban? {
    if (this == null) return null

    return io.github.sophon.domain.model.Ban(
        userId = userId,
        bannedAt = Instant.fromEpochMilliseconds(bannedAt),
        expiresAt = Instant.fromEpochMilliseconds(expiresAt),
        preventBotUsage = preventBotUsage == 1L,
    )
}
