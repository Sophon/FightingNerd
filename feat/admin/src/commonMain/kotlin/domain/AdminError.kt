package domain

import io.github.sophon.core.domain.Error

enum class AdminError: Error {
    DATABASE_ERROR,

    USER_ALREADY_BANNED,
}