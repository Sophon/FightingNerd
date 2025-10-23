package com.example.glossaryinfil

import com.example.core.domain.Error

enum class GlossaryError: Error {
    EMPTY_GLOSSARY,
    ERROR_DOWNLOADING_DATA,
}