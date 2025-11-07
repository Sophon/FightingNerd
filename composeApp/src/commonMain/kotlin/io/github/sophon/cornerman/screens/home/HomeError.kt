package io.github.sophon.cornerman.screens.home

import io.github.sophon.core.domain.Error

internal enum class HomeError: Error {
    UNKNOWN_CHARACTER,
    DOWNLOAD_ERROR,
    IO_ERROR,

    UNKNOWN,
}

internal fun WavuError.toDomain(): HomeError {
    return when (this) {
        WavuError.UNKNOWN_CHARACTER -> HomeError.UNKNOWN_CHARACTER
        WavuError.DOWNLOAD_ERROR-> HomeError.DOWNLOAD_ERROR
        WavuError.UNKNOWN_MOVE,
        WavuError.DATABASE_ERROR -> HomeError.UNKNOWN
    }
}