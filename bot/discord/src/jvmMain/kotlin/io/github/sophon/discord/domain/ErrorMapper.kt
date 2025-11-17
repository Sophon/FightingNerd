package io.github.sophon.discord.domain

import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.discord.BotError

internal fun WikiError.toDomainError(): BotError {
    return when (this) {
        is WikiError.UnknownCharacter -> BotError.UnknownCharacter(inputs[0])
        is WikiError.UnknownMove -> BotError.UnknownMove(*inputs)
        is WikiError.DownloadError -> BotError.DownloadError(inputs[0])
        is WikiError.DatabaseError -> BotError.Unknown(inputs.getOrNull(0) ?: "")
    }
}
