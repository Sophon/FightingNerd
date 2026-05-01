package io.github.sophon.glossaryinfil.integration

import io.github.sophon.core.domain.Error

enum class GlossaryError: Error {
    EMPTY_GLOSSARY,
    ERROR_DOWNLOADING_DATA,
}