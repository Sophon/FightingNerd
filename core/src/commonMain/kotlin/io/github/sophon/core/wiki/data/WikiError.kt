package io.github.sophon.core.wiki.data

import io.github.sophon.core.domain.Error

sealed class WikiError(vararg val inputs: String) : Error {
    class DownloadError(input: String) : WikiError(input)
    class DatabaseError(input: String) : WikiError(input)
    class UnknownCharacter(input: String) : WikiError(input)
    class UnknownMove(vararg inputs: String) : WikiError(*inputs)

    override fun toString(): String =
        "${this::class.simpleName}(${inputs.joinToString()})"
}
