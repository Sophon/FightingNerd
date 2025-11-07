package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.Error

enum class WikiDataError: Error {
    DOWNLOAD_ERROR,
    DATABASE_ERROR,
}