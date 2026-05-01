package io.github.sophon.integration.model

import io.github.sophon.core.domain.Error

sealed class StatsError(vararg val errors: String) : Error {
    class FileError(vararg errors: String) : StatsError(*errors)
    class SerializationError(vararg errors: String) : StatsError(*errors)
    class Unknown(vararg errors: String) : StatsError(*errors)
}