package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.Error

enum class WikiError: Error {
    DOWNLOAD_ERROR,
    DATABASE_ERROR,
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
}