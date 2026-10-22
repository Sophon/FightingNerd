package io.github.sophon.discord.app.service

import io.github.sophon.discord.app.outPort.RefreshWikiPort
import io.github.sophon.discord.inPort.RefreshWikiUseCase

internal class RefreshWikiService(
    private val refreshWikiPort: RefreshWikiPort,
): RefreshWikiUseCase {
    override suspend fun invoke() {
        refreshWikiPort.refresh()
    }
}
