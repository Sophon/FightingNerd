package com.example.cornerman.screens.moveList

import com.example.core.domain.Error

enum class MoveListError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    CHARACTER_LIST_NOT_FOUND,
    CHARACTER_SERIALIZATION_ERROR,
    DOWNLOAD_ERROR,

    UNKNOWN,
}