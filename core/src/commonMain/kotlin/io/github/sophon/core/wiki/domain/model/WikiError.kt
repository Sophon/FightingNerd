package io.github.sophon.core.wiki.domain.model

import io.github.sophon.core.domain.Error

enum class WikiError: Error {
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
}