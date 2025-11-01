package io.github.sophon.cornerman.screens.home

import io.github.sophon.core.domain.Error
import io.github.sophon.wikiwavu.WavuError

internal enum class HomeError: Error {
    UNKNOWN_CHARACTER,
    CHARACTER_LIST_NOT_FOUND,
    DOWNLOAD_ERROR,

    UNKNOWN,
}

internal fun WavuError.toDomain(): HomeError {
    return when (this) {
        WavuError.UNKNOWN_CHARACTER -> HomeError.UNKNOWN_CHARACTER
        WavuError.CHARACTER_LIST_NOT_FOUND -> HomeError.CHARACTER_LIST_NOT_FOUND
        WavuError.CHARACTER_SERIALIZATION_ERROR,
        WavuError.DOWNLOAD_ERROR-> HomeError.DOWNLOAD_ERROR
        WavuError.UNKNOWN_MOVE,
        WavuError.DATABASE_ERROR -> HomeError.UNKNOWN
    }
}