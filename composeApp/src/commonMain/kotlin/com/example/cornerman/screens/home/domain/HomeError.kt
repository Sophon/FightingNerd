package com.example.cornerman.screens.home.domain

import com.example.core.domain.Error

enum class HomeError: Error {
    UNKNOWN_CHARACTER,
    CHARACTER_LIST_NOT_FOUND,
    CHARACTER_SERIALIZATION_ERROR,
    DOWNLOAD_ERROR,

    UNKNOWN,
}