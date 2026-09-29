package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.discord.app.port.outbound.StartWikiPort
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.wiki.application.domain.model.WikiConfig
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
): StartWikiPort {
    override suspend fun startWiki(): EmptyResult<BotError> {
        val wikiConfig: WikiConfig = WikiConfig()
        configureWikiUseCase(wikiConfig)
    }
}
