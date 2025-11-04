package io.github.sophon.discord.domain

import io.github.sophon.discord.BotError
import io.github.sophon.wikiwavu.WavuError

internal fun WavuError.toDomain(): BotError {
    return when (this) {
        WavuError.UNKNOWN_CHARACTER -> BotError.UNKNOWN_CHARACTER
        WavuError.UNKNOWN_MOVE -> BotError.UNKNOWN_MOVE
        WavuError.DOWNLOAD_ERROR -> BotError.DOWNLOAD_ERROR
        WavuError.CHARACTER_LIST_NOT_FOUND,
        WavuError.CHARACTER_SERIALIZATION_ERROR,
            -> BotError.CHARACTER_LIST_FILE_ERROR
        WavuError.DATABASE_ERROR -> BotError.UNKNOWN
    }
}