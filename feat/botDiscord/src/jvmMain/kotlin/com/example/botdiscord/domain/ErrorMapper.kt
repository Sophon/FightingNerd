package com.example.botdiscord.domain

import com.example.botdiscord.BotError
import com.example.wikiwavu.WavuError

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