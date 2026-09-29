package io.github.sophon.discord.adapter.outbound.wiki

import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.mapError
import io.github.sophon.discord.app.domain.model.DiscordJsonConfig
import io.github.sophon.discord.app.port.outbound.ConfigureWikiPort
import io.github.sophon.discord.app.port.outbound.RefreshWikiPort
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.wiki.application.port.inbound.ConfigureWikiUseCase
import io.github.sophon.wiki.application.port.inbound.RefreshDataUseCase
import kotlinx.coroutines.flow.collect

internal class WikiAdapter(
    private val configureWikiUseCase: ConfigureWikiUseCase,
    private val refreshDataUseCase: RefreshDataUseCase,
): ConfigureWikiPort, RefreshWikiPort {
    override suspend fun configure(discordJsonConfig: DiscordJsonConfig): EmptyResult<BotError> {
        val result = discordJsonConfig.toWikiConfig()
            .flatMap { wikiConfig -> configureWikiUseCase(wikiConfig) }
            .mapError { it.toDomainError() }
        return result
    }

    override suspend fun refresh() {
        refreshDataUseCase().collect()
    }
}
