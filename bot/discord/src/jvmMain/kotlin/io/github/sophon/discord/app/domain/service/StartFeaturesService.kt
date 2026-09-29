package io.github.sophon.discord.app.domain.service

import io.github.aakira.napier.Napier
import io.github.sophon.core.architecture.EmptyResult
import io.github.sophon.core.architecture.Result
import io.github.sophon.core.architecture.flatMap
import io.github.sophon.core.architecture.map
import io.github.sophon.core.architecture.onSuccess
import io.github.sophon.discord.app.domain.model.DiscordJsonConfig
import io.github.sophon.discord.app.port.inbound.StartFeaturesUseCase
import io.github.sophon.discord.app.port.outbound.ReadFilePort
import io.github.sophon.discord.app.port.outbound.ConfigureWikiPort
import io.github.sophon.discord.app.port.outbound.RefreshWikiPort
import io.github.sophon.discord.feat.core.domain.model.BotError
import kotlinx.serialization.json.Json

internal class StartFeaturesService(
    private val json: Json,
    private val readFilePort: ReadFilePort,
    private val configureWikiPort: ConfigureWikiPort,
    private val refreshWikiPort: RefreshWikiPort,
): StartFeaturesUseCase {
    override suspend fun invoke(): EmptyResult<BotError> {
        val result = loadConfig()
            .flatMap { discordJsonConfig ->
                Napier.i(tag = TAG) { "JSON config: $discordJsonConfig" }
                startWiki(discordJsonConfig)
            }

        return result
    }

    private fun loadConfig(): Result<DiscordJsonConfig, BotError> {
        val result = readFilePort.read(CONFIG_PATH)
            .map { configText ->
                json.decodeFromString<DiscordJsonConfig>(configText)
            }
        return result
    }

    private suspend fun startWiki(discordJsonConfig: DiscordJsonConfig): EmptyResult<BotError> {
        val result = configureWikiPort.configure(discordJsonConfig)
            .onSuccess { refreshWikiPort.refresh() }

        return result
    }


    private companion object {
        const val CONFIG_PATH = "res/discordConfig.json"
        const val TAG = "StartFeaturesService"
    }
}
