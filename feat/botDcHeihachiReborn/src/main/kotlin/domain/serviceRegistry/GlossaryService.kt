package domain.serviceRegistry

import MAX_LENGTH_EMBED
import com.example.core.domain.Result
import com.example.core.util.truncate
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import model.GlossaryItem
import usecase.SearchGlossaryUseCase
import usecase.StartGlossaryUseCase
import util.createErrorEmbed
import util.field
import util.replaceItalic
import util.replaceUnderline

internal class GlossaryService(
    private val startGlossaryUseCase: StartGlossaryUseCase,
    private val searchGlossaryUseCase: SearchGlossaryUseCase,
): RegisteredService {
    override val mainCommand: Command = Command.GL
    override val serviceInfo = ServiceInfo(
        name = "Infil Glossary",
        url = "https://glossary.infil.net/",
        iconUrl = "https://i.imgur.com/0cnTzNk.png",
    )
    override val slashCommands = listOf(
        SlashCommand(
            name = Command.GL,
            description = "Fighting-game glossary",
            arguments = listOf(
                SlashCommand.Argument(
                    name = KEY_TERM,
                    description = "Term"
                )
            )
        )
    )

    override suspend fun start() {
        startGlossaryUseCase.invoke()
    }

    override suspend fun execute(
        command: Command,
        vararg args: String
    ): EmbedBuilder.() -> Unit {
        val result = searchGlossaryUseCase.invoke(
            args.joinToString(" ")
        )

        return when (result) {
            is Result.Success -> {
                createEmbed(item = result.data)
            }
            is Result.Error -> {
                createErrorEmbed(error = result.error)
            }
        }
    }


    private fun createEmbed(
        item: GlossaryItem
    ): EmbedBuilder.() -> Unit = {
        val formattedItem = item.format()
        title = formattedItem.term
        color = Color(BROWN)

        field(
            name = "",
            value = formattedItem.definition
                .replaceUnderline()
                .truncate(MAX_LENGTH_EMBED),
            inline = false
        )

        val japaneseValueString = formattedItem.jpTranslation
            .joinToString(separator = "") { "* $it\n" }
        field(name = "🇯🇵", value = japaneseValueString, inline = false)

        footer {
            text = serviceInfo.name
            icon = serviceInfo.iconUrl
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