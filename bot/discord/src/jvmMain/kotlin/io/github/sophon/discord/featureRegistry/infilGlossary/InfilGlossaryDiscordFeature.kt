package io.github.sophon.discord.featureRegistry.infilGlossary

import MAX_LENGTH_EMBED
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.domain.Result
import io.github.sophon.core.domain.map
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.BotError
import io.github.sophon.discord.featureRegistry.Command
import io.github.sophon.discord.featureRegistry.DiscordRegisteredFeature
import io.github.sophon.discord.featureRegistry.SupportedCommand
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.SearchGlossaryUseCase
import io.github.sophon.discord.featureRegistry.infilGlossary.usecase.StartGlossaryUseCase
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.discord.util.replaceItalic
import io.github.sophon.discord.util.replaceUnderline
import io.github.sophon.glossaryinfil.domain.GlossaryItem
import io.github.sophon.glossaryinfil.domain.InfilUrlProvider

internal class InfilGlossaryDiscordFeature(
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
    private val urlProvider: InfilUrlProvider,
): DiscordRegisteredFeature {
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
    override val featureInfo = FeatureInfo(
        name = "Infil Glossary",
        url = "https://glossary.infil.net/",
        iconUrl = "https://i.imgur.com/OigKJBY.png",
    )
    override val otherCommands = listOf<SupportedCommand>()

    override suspend fun start() {
        startGlossaryUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return when (command) {
            Command.GL -> searchTerm(query)
            else -> Result.Error(BotError.BOT_LOGIC_ERROR)
        }
    }


    private suspend fun searchTerm(
        query: String,
    ): Result<EmbedBuilder.() -> Unit, BotError> {
        return searchGlossaryUseCase.invoke(query)
            .map { createEmbed(it) }
    }

    private fun createEmbed(
        item: GlossaryItem
    ): EmbedBuilder.() -> Unit = {
        val formattedItem = item.format()
        title = formattedItem.term
        url = urlProvider.termUrl(item)
        color = Color(BROWN)

        mandatoryField(
            name = "",
            value = formattedItem.definition
                .replaceUnderline()
                .truncate(MAX_LENGTH_EMBED),
            inline = false
        )

        val japaneseValueString = formattedItem.jpTranslation
            .joinToString(separator = "") { "* $it\n" }
        mandatoryField(name = "🇯🇵", value = japaneseValueString, inline = false)

        optionalField(name = "Video", value = "[Link](${url})")

        footer {
            text = featureInfo.name
            icon = featureInfo.iconUrl
        }
    }

    private fun GlossaryItem.format(): GlossaryItem {
        return this.copy(
            definition = this.definition.replaceUnderline(),
            jpTranslation = this.jpTranslation.map { it.replaceItalic() }
        )
    }
}

private const val KEY_TERM = "term"
private const val BROWN = 0xDAA06D