package com.example.glossaryinfil

import io.github.sophon.core.domain.Error

enum class GlossaryError: Error {
    EMPTY_GLOSSARY,
    ERROR_DOWNLOADING_DATA,
}