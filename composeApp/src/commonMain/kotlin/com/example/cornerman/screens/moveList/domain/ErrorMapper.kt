package com.example.cornerman.screens.moveList.domain

import com.example.wikiwavu.WavuError

internal fun WavuError.toDomain(): MoveListError {
    return when (this) {
        WavuError.UNKNOWN_CHARACTER -> MoveListError.UNKNOWN_CHARACTER
        WavuError.UNKNOWN_MOVE -> MoveListError.UNKNOWN_MOVE
        WavuError.CHARACTER_LIST_NOT_FOUND -> MoveListError.CHARACTER_LIST_NOT_FOUND
        WavuError.DOWNLOAD_ERROR -> MoveListError.DOWNLOAD_ERROR
        WavuError.CHARACTER_SERIALIZATION_ERROR -> MoveListError.UNKNOWN
    }
}