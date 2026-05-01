package io.github.sophon.fightingnerd.screens.moveList.domain

import io.github.sophon.core.domain.Error
import io.github.sophon.core.wiki.data.WikiError

internal enum class MoveListError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    DOWNLOAD_ERROR,

    DATABASE_ERROR,

    UNKNOWN,
}

internal fun WikiError.toDomainError(): MoveListError {
    return when(this) {
        is WikiError.UnknownCharacter -> MoveListError.UNKNOWN_CHARACTER
        is WikiError.UnknownMove -> MoveListError.UNKNOWN_MOVE
        is WikiError.DatabaseError -> MoveListError.DATABASE_ERROR
        is WikiError.DownloadError -> MoveListError.DOWNLOAD_ERROR
    }
}