package io.github.sophon.discord.app.service

import io.github.sophon.discord.app.outPort.RefreshGlossaryPort
import io.github.sophon.discord.inPort.RefreshGlossaryUseCase

internal class RefreshGlossaryService(
    private val refreshGlossaryPort: RefreshGlossaryPort,
): RefreshGlossaryUseCase {
    override suspend fun invoke() {
        refreshGlossaryPort.refresh()
    }
}
