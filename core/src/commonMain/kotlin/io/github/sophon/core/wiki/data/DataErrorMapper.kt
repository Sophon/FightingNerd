package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.DataError

fun DataError.toDomainError(): WikiError {
    val error = when (this) {
        is DataError.Local -> WikiError.DatabaseError(this.toString())
        DataError.Remote.PAGE_NOT_FOUND -> WikiError.PageNotFound(this.toString())
        is DataError.Remote -> WikiError.DownloadError(this.toString())
    }
    return error
}
