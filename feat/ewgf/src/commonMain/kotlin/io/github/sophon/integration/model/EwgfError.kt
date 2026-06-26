package io.github.sophon.integration.model

import io.github.sophon.core.architecture.Error

sealed class EwgfError(vararg val errors: String): Error {
    class DatabaseError(error: String): EwgfError(error)
    class PlayerNotRegistered(id: String): EwgfError(id)
    class PlayerNotFound(id: String): EwgfError(id)
    class LogicError(error: String): EwgfError(error)

    override fun toString(): String =
        "${this::class.simpleName}(${errors.joinToString()})"
}
