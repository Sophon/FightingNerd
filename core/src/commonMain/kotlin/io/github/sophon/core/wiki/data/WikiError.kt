package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.Error

sealed class WikiError(private val name: String, vararg val inputs: String) : Error {
    class DownloadError(input: String) : WikiError("DownloadError", input)
    class PageNotFound(input: String) : WikiError("PageNotFound", input)
    class DatabaseError(input: String) : WikiError("DatabaseError", input)
    class UnknownCharacter(input: String) : WikiError("UnknownCharacter", input)
    class UnknownMove(vararg inputs: String) : WikiError("UnknownMove", *inputs)

    override fun toString(): String {
        return "$name(${inputs.joinToString()})"
    }
}
