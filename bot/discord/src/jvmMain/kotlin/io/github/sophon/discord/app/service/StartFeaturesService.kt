package io.github.sophon.discord.app.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.onError
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.app.model.BotError
import io.github.sophon.discord.app.model.DiscordConfig
import io.github.sophon.discord.inPort.StartFeaturesUseCase
import io.github.sophon.discord.app.outPort.ConfigureAdminPort
import io.github.sophon.discord.app.outPort.ConfigureWikiPort
import io.github.sophon.discord.app.outPort.LoadConfigPort
import io.github.sophon.discord.app.outPort.RefreshWikiPort
import io.github.sophon.discord.app.outPort.StatsPort

internal class StartFeaturesService(
    private val loadConfigPort: LoadConfigPort,
    private val configureAdminPort: ConfigureAdminPort,
    private val configureWikiPort: ConfigureWikiPort,
    private val refreshWikiPort: RefreshWikiPort,
    private val statsPort: StatsPort,
): StartFeaturesUseCase {
    override suspend fun invoke(): EmptyResult<BotError> {
        statsPort.configure()
            .onError { error -> Napier.e(tag = TAG) { "Stats configuration failed: $error" } }

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
