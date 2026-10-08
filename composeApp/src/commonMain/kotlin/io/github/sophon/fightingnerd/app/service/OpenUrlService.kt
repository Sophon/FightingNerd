package io.github.sophon.fightingnerd.app.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.app.model.AppError
import io.github.sophon.fightingnerd.app.outPort.UrlPort
import io.github.sophon.fightingnerd.inPort.OpenUrlUseCase

@ExcludeFromCoverage("plain port call")
internal class OpenUrlService(
    private val urlPort: UrlPort,
): OpenUrlUseCase {
    override fun invoke(url: String): EmptyResult<AppError> {
        return urlPort.openUrl(url)
    }
}
