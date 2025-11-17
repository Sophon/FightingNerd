package io.github.sophon.wikiSuperCombo.domain

import io.github.sophon.core.domain.Error

enum class SuperComboError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    CHARACTER_LIST_NOT_FOUND,
    CHARACTER_SERIALIZATION_ERROR,
    DOWNLOAD_ERROR,
    DATABASE_ERROR,
}