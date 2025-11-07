package io.github.sophon.wikiwavu

import io.github.sophon.core.domain.Error

enum class WavuError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    DOWNLOAD_ERROR,
    DATABASE_ERROR,
}