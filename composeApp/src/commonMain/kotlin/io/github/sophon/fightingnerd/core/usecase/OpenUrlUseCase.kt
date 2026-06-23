package io.github.sophon.fightingnerd.core.usecase

import io.github.sophon.fightingnerd.core.domain.UrlOpener

internal class OpenUrlUseCase(
    private val urlOpener: UrlOpener,
) {
    fun invoke(url: String) {
        urlOpener.openUrl(url)
    }
}
