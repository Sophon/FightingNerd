package io.github.sophon.discord.app.domain.service

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.port.inbound.StartFeaturesUseCase
import io.github.sophon.discord.app.port.outbound.StartWikiPort
import io.github.sophon.discord.feat.core.domain.model.BotError

internal class StartFeaturesService(
    private val startWikiPort: StartWikiPort,
): StartFeaturesUseCase {
    override suspend fun invoke(): EmptyResult<BotError> {
        val result = startWikiPort.startWiki()
        return result
    }
}
