package com.example.botdiscord

import com.example.core.domain.Error

enum class BotError: Error {
    INVALID_QUERY,
    UNKNOWN_CHARACTER,
    UNKNOWN_MOVE,
    EMPTY_GLOSSARY,
    GLOSSARY_TERM_NOT_FOUND,
    CHARACTER_LIST_FILE_ERROR,
    DOWNLOAD_ERROR,

    BOT_LOGIC_ERROR,
}