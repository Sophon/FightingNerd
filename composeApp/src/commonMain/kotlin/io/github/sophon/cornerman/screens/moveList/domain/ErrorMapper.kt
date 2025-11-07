package io.github.sophon.cornerman.screens.moveList.domain

internal fun WavuError.toDomain(): MoveListError {
    return when (this) {
        WavuError.UNKNOWN_CHARACTER -> MoveListError.UNKNOWN_CHARACTER
        WavuError.UNKNOWN_MOVE -> MoveListError.UNKNOWN_MOVE
        WavuError.DOWNLOAD_ERROR -> MoveListError.DOWNLOAD_ERROR
        WavuError.DATABASE_ERROR -> MoveListError.DATABASE_ERROR
    }
}