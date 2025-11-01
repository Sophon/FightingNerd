package io.github.sophon.core.domain

sealed interface DataError: Error {
    enum class Local: DataError {
        UNKNOWN,
        DISK_FULL,
        INSUFFICIENT_FUNDS
    }

    enum class Remote: DataError {
        UNKNOWN,
        TOO_MANY_REQUESTS,
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALIZATION_ERROR,
    }
}