package io.github.sophon.fightingnerd.core.usecase

import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.fightingnerd.core.domain.UrlOpener

@ExcludeFromCoverage("native code")
internal class OpenUrlUseCase(
    private val urlOpener: UrlOpener,
) {
    operator fun invoke(url: String) {
        urlOpener.openUrl(url)
    }
}
