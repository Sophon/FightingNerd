package io.github.sophon.discord.domain

import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.discord.BotError

internal fun WikiError.toDomainError(): BotError {
    return when (this) {
        WikiError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
        WikiError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
        WikiError.DOWNLOAD_ERROR -> BotError.DOWNLOAD_ERROR
        WikiError.DATABASE_ERROR -> BotError.UNKNOWN
    }
}