package io.github.sophon.fightingnerd.core.model

import io.github.sophon.core.domain.Error

internal sealed class AppError(private val errorMessage: String) : Error {
    internal data class ConfigNotFoundError(val error: String) : AppError(errorMessage = error)
    internal data class ConfigParseError(val error: String) : AppError(errorMessage = error)
}