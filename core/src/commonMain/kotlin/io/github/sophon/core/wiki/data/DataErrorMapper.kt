package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.DataError

fun DataError.Remote.toDomainError(): WikiError {
    return WikiError.DownloadError(this.toString())
}