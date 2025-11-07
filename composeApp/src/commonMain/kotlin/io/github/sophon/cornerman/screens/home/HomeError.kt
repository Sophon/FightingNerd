package io.github.sophon.cornerman.screens.home

import io.github.sophon.core.domain.Error
import io.github.sophon.core.wiki.data.WikiError

internal enum class HomeError: Error {
    UNKNOWN_CHARACTER,
    DOWNLOAD_ERROR,
    IO_ERROR,

    UNKNOWN,
}

internal fun WikiError.toDomainError(): HomeError {
    return when (this) {
        WikiError.UNKNOWN_CHARACTER -> HomeError.UNKNOWN_CHARACTER
        WikiError.DOWNLOAD_ERROR-> HomeError.DOWNLOAD_ERROR
        WikiError.UNKNOWN_MOVE,
        WikiError.DATABASE_ERROR -> HomeError.UNKNOWN
    }
}