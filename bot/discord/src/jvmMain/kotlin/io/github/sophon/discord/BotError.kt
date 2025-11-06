package io.github.sophon.discord

import io.github.sophon.core.domain.Error

enum class BotError: Error {
    INVALID_QUERY,
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    EMPTY_GLOSSARY,
    GLOSSARY_TERM_NOT_FOUND,
    DOWNLOAD_ERROR,

    BOT_LOGIC_ERROR,

    UNKNOWN,
}