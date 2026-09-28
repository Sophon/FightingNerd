package io.github.sophon.wiki.application.domain.model

import io.github.sophon.core.architecture.DataError

internal fun DataError.toWikiError(): WikiError {
    val error = when (this) {
        is DataError.Local -> WikiError.DatabaseError(this.toString())
        DataError.Remote.PAGE_NOT_FOUND -> WikiError.PageNotFound(this.toString())
        is DataError.Remote -> WikiError.DownloadError(this.toString())
    }
    return error
}
