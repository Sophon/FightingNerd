package io.github.sophon.fightingnerd.core.util

import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.mapError
import io.github.sophon.core.wiki.data.WikiError
import io.github.sophon.fightingnerd.core.model.AppError

internal fun <T> Result<T, WikiError>.mapWikiError(): Result<T, AppError> {
    return mapError { wikiError ->
        AppError.WikiError(wikiError.toString())
    }
}