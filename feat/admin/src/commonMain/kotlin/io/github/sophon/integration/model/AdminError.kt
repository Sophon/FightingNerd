package io.github.sophon.integration.model

import io.github.sophon.core.architecture.Error

sealed class AdminError(vararg val errors: String): Error {
    class DatabaseError(error: String): AdminError(error)
    class PermissionDenied: AdminError("Permission denied")
    class WrongReplyFormat(val query: String): AdminError(query)
    class UserBanned(val id: String): AdminError(id)

    override fun toString(): String =
        "${this::class.simpleName}(${errors.joinToString()})"
}