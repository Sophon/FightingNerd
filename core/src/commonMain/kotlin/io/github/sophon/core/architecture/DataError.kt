package io.github.sophon.core.architecture

sealed interface DataError: Error {
    enum class Local: DataError {
        UNKNOWN,
        DISK_FULL,
        INSUFFICIENT_FUNDS
    }

    enum class Remote: DataError {
        UNKNOWN,
        PAGE_NOT_FOUND,
        TOO_MANY_REQUESTS,
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALIZATION_ERROR,
    }
}