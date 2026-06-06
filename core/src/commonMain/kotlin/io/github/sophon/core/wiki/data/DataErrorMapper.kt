package io.github.sophon.core.wiki.data

import io.github.sophon.core.architecture.DataError

fun DataError.Remote.toDomainError(
    tag: String,
): WikiError {
    return WikiError.DownloadError(this.toString())
}