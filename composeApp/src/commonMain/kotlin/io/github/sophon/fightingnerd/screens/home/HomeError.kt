package io.github.sophon.fightingnerd.screens.home

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
        is WikiError.UnknownCharacter -> HomeError.UNKNOWN_CHARACTER
        is WikiError.DownloadError-> HomeError.DOWNLOAD_ERROR
        is WikiError.UnknownMove,
        is WikiError.DatabaseError -> HomeError.UNKNOWN
    }
}