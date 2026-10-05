package io.github.sophon.discord.app.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.app.domain.model.BotError
import io.github.sophon.discord.app.domain.model.DiscordConfig
import io.github.sophon.discord.app.port.inbound.StartFeaturesUseCase
import io.github.sophon.discord.app.port.outbound.ConfigureAdminPort
import io.github.sophon.discord.app.port.outbound.ConfigureWikiPort
import io.github.sophon.discord.app.port.outbound.LoadConfigPort
import io.github.sophon.discord.app.port.outbound.RefreshWikiPort

internal class StartFeaturesService(
    private val loadConfigPort: LoadConfigPort,
    private val configureAdminPort: ConfigureAdminPort,
    private val configureWikiPort: ConfigureWikiPort,
    private val refreshWikiPort: RefreshWikiPort,
): StartFeaturesUseCase {
    override suspend fun invoke(): EmptyResult<BotError> {
        val result = loadConfigPort.load()
            .flatMap { discordJsonConfig ->
                Napier.i(tag = TAG) { "JSON config: $discordJsonConfig" }
                val startResult = configureAdminPort.configure(discordJsonConfig)
                    .flatMap { startWiki(discordJsonConfig) }
                startResult
            }

        return result
    }

    private suspend fun startWiki(discordConfig: DiscordConfig): EmptyResult<BotError> {
        val result = configureWikiPort.configure(discordConfig)
            .onSuccess { refreshWikiPort.refresh() }

        return result
    }


    private companion object {
        const val TAG = "StartFeaturesService"
    }
}
