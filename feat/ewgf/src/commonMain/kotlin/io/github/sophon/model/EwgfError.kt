package io.github.sophon.model

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Error

sealed interface EwgfError: Error {
    data object PlayerNotRegistered: EwgfError
    data class Database(val error: DataError): EwgfError
    data class Download(val error: DataError.Remote): EwgfError
}
