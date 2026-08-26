package io.github.sophon.fightingnerd.core.model

import io.github.sophon.core.architecture.Error

internal sealed class AppError(val errorMessage: String) : Error {
    internal data class ConfigNotFoundError(val error: String) : AppError(errorMessage = error)
    internal data class ConfigParseError(val error: String) : AppError(errorMessage = error)
    internal data class WikiClientNotFound(val name: String) : AppError(errorMessage = name)
    internal data class WikiError(val error: String) : AppError(errorMessage = error)
    internal data class GameNotFound(val game: String): AppError(errorMessage = game)
    internal data class IOError(val error: String): AppError(errorMessage = error)

    internal data object Unknown: AppError("Unknown")
}