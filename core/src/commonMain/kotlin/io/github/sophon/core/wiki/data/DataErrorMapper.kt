package io.github.sophon.core.wiki.data

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.DataError

fun DataError.Remote.toDomainError(
    tag: String,
): WikiError {
    Napier.e(tag = tag) { toString() }
    return WikiError.DownloadError("TODO: toDomainError()")
}