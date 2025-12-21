package io.github.sophon.discord.featureRegistry.infilGlossary

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.aakira.napier.Napier
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.BotError
import io.github.sophon.discord.MAX_LENGTH_EMBED
import io.github.sophon.discord.featureRegistry.BotOutput
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.GetInfilFeatureInfoUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.domain.Source
import io.github.sophon.glossaryinfil.domain.GlossaryItem
import io.github.sophon.glossaryinfil.domain.InfilUrlProvider

internal class InfilGlossaryDiscordFeature(
    getInfilFeatureInfoUseCase: GetInfilFeatureInfoUseCase,
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val urlProvider: InfilUrlProvider,
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
            Command.GL -> searchTerm(query)
            else -> Result.Error(BotError.BotLogicError(command.name, query))
        }
    }


    private suspend fun searchTerm(
        query: String,
    ): Result<BotOutput, BotError> {
        return searchGlossaryUseCase.invoke(query)
            .map { BotOutput(embedBuilder = createEmbed(it)) }
    }

    private fun createEmbed(
        item: GlossaryItem
    ): EmbedBuilder.() -> Unit = {
        title = item.term
        url = urlProvider.termUrl(item)
        color = Color(BROWN)

        mandatoryField(
            name = "",
            value = item.definition
                .truncate(MAX_LENGTH_EMBED),
            inline = false
        )

        val japaneseValueString = item.jpTranslation
            .joinToString(separator = "") { "* $it\n" }
        mandatoryField(name = "🇯🇵", value = japaneseValueString, inline = false)

        optionalField(name = "Video", value = "[Link](${url})")

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }


    private companion object {
        const val TAG = "InfilGlossaryDiscordFeature"
        const val KEY_TERM = "term"
        const val BROWN = 0xDAA06D
    }
}
