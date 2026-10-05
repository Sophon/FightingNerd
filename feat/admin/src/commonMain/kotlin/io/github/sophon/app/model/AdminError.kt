package io.github.sophon.app.model

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Error

sealed interface AdminError: Error {
    data object PermissionDenied: AdminError
    data class Database(val error: DataError): AdminError
}
