package io.github.sophon.core.wiki.data

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.DataError

internal fun DataError.Remote.toDomainError(
    tag: String,
): WikiError {
    Napier.e(tag = tag) { toString() }
    return WikiError.DOWNLOAD_ERROR
}