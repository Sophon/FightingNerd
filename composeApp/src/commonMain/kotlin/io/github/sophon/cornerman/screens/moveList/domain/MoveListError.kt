package io.github.sophon.cornerman.screens.moveList.domain

import io.github.sophon.core.domain.Error
import io.github.sophon.core.wiki.data.WikiError

enum class MoveListError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    DOWNLOAD_ERROR,

    DATABASE_ERROR,

    UNKNOWN,
}

internal fun WikiError.toDomainError(): MoveListError {
    return when(this) {
        WikiError.UNKNOWN_CHARACTER -> MoveListError.UNKNOWN_CHARACTER
        WikiError.UNKNOWN_MOVE -> MoveListError.UNKNOWN_MOVE
        WikiError.DATABASE_ERROR -> MoveListError.DATABASE_ERROR
        WikiError.DOWNLOAD_ERROR -> MoveListError.DOWNLOAD_ERROR
    }
}