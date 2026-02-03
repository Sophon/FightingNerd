package ewgf.domain

import io.github.sophon.core.domain.Error

sealed class EwgfError(vararg val errors: String): Error {
    class DatabaseError(error: String): EwgfError(error)
    class PlayerNotFound(query: String): EwgfError(query)
    class PlayerAlreadyRegistered(query: String): EwgfError(query)

    override fun toString(): String =
        "${this::class.simpleName}(${errors.joinToString()})"
}