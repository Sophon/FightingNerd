package io.github.sophon.data

import io.github.sophon.admin.data.Ban
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun Ban?.toDomain(): io.github.sophon.app.domain.model.Ban? {
    if (this == null) return null

    return io.github.sophon.app.domain.model.Ban(
        offenderId = offenderId,
        bannedAt = Instant.fromEpochMilliseconds(bannedAt),
        expiresAt = Instant.fromEpochMilliseconds(expiresAt),
        issuerId = authorId,
        preventBotUsage = preventBotUsage == 1L,
    )
}
