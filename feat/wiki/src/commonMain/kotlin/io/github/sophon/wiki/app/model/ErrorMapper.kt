package io.github.sophon.wiki.app.model

import io.github.sophon.core.architecture.DataError
import io.github.sophon.wiki.model.WikiError

internal fun DataError.toWikiError(): WikiError {
    val error = when (this) {
        is DataError.Local -> WikiError.DatabaseError(this.toString())
        DataError.Remote.PAGE_NOT_FOUND -> WikiError.PageNotFound(this.toString())
        is DataError.Remote -> WikiError.DownloadError(this.toString())
    }
    return error
}
