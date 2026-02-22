package io.github.sophon.discord.featureRegistry.infilGlossary

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.model.BotOutput
import io.github.sophon.discord.domain.model.Command
import io.github.sophon.discord.domain.model.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.domain.Source

internal class InfilGlossaryDiscordFeature(
    getInfilFeatureInfoUseCase: GetInfilFeatureInfoUseCase,
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
): DiscordRegisteredFeature {
    override val featureInfo = getInfilFeatureInfoUseCase.invoke()
    override val defaultCommand = Command.Gl
    override val otherCommands = listOf<Command>()

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        startGlossaryUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ): Result<BotOutput, BotError> {
        return when (command) {
            Command.Gl -> searchTerm(query)
            else -> Result.Error(
                BotError.BotLogicError(
                    command.name,
                    query,
                )
            )
        }
    }


    private suspend fun searchTerm(
        query: String,
    ): Result<BotOutput, BotError> {
        return searchGlossaryUseCase.invoke(query)
            .map { item ->
                BotOutput(primaryEmbedBuilder = glossaryEmbed(item, featureInfo))
            }
    }


    private companion object {
        const val TAG = "InfilGlossaryDiscordFeature"
    }
}
