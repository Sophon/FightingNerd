package io.github.sophon.glossaryinfil.model

import io.github.sophon.core.architecture.DataError
import io.github.sophon.core.architecture.Error

sealed interface GlossaryError: Error {
    data object EmptyGlossary: GlossaryError
    data class Download(val error: DataError.Remote): GlossaryError
    data class Database(val error: DataError.Local): GlossaryError
}
