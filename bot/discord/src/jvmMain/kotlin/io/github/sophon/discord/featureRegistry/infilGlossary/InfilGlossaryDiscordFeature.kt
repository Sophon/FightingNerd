package io.github.sophon.discord.featureRegistry.infilGlossary

import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.discord.BotError
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.domain.Command
import io.github.sophon.discord.domain.DiscordRegisteredFeature
import io.github.sophon.discord.domain.SupportedCommand
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.domain.Source
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class InfilGlossaryDiscordFeature(
    getInfilFeatureInfoUseCase: GetInfilFeatureInfoUseCase,
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
): DiscordRegisteredFeature {
    override val featureInfo = getInfilFeatureInfoUseCase.invoke()
    override val defaultCommand = SupportedCommand(
        command = Command.GL,
        description = "Fighting-game glossary",
        arguments = listOf(
            SupportedCommand.Argument(
                name = KEY_TERM,
                description = "Term"
            )
        )
    )
    override val otherCommands = listOf<SupportedCommand>()
    private val _events = MutableSharedFlow<Result<BotOutput, BotError>>()
    override val events: SharedFlow<Result<BotOutput, BotError>> = _events.asSharedFlow()

    override suspend fun start() {
        Napier.d(tag = TAG) { "Starting: $featureInfo" }

        startGlossaryUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        query: String,
        origin: Source,
    ) {
        val result = when (command) {
            Command.GL -> searchTerm(query)
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }

        _events.emit(result)
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
        const val KEY_TERM = "term"
    }
}
