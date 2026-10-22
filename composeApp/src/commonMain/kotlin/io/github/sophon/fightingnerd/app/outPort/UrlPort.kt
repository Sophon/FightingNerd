package io.github.sophon.fightingnerd.app.outPort

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.fightingnerd.app.model.AppError

internal interface UrlPort {
    fun openUrl(url: String): EmptyResult<AppError>
}
