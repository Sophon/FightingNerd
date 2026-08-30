package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.BotOutput
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.util.mandatoryField

@ExcludeFromCoverage("UI")
internal class CreateErrorEmbedBuilderUseCase(
    private val commandRegistry: CommandRegistry,
) {
    fun invoke(error: BotError): BotOutput.MutableEmbedBuilder {

        val result = when (error) {
            is BotError.UnknownCharacter,
            is BotError.UnknownMove -> syntaxErrorEmbed(error)
            is BotError.InvalidQuery -> createQueryErrorEmbed(error)
            is BotError.InvalidCommand -> createCommandErrorEmbed(error)

            else -> createGenericError(error)
        }
        return result
    }

    private fun createGenericError(error: BotError): BotOutput.MutableEmbedBuilder {
        val errorDescription = error.toString().truncate(EMBED_MAX_LENGTH)
        val primaryBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)
            description = errorDescription
        }
        val leftOverBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)
            description = errorDescription
        }

        return BotOutput.MutableEmbedBuilder(primaryBuilder, leftOverBuilder)
    }

    private fun syntaxErrorEmbed(
        unknownCharacterError: BotError,
    ): BotOutput.MutableEmbedBuilder {
        val errorText = unknownCharacterError
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH)
        val errorDescription = createErrorDescription()

        val primaryBuilder: EmbedBuilder.() -> Unit ={
            title = "ERROR"
            color = Color(RED)
            description = errorText

            mandatoryField(
                name = "SYNTAX:",
                value = "1. `[character name]` `[move input]`\n" +
                        "   - `character name` →  __**NO SPACES**__\n" +
                        "   - `move input`\n" +
                        "   - `lee df1`, `ak h.bad.32`, `ling bt.4`, `ken 236pp` etc.\n\n" +
                        "2. `[character name]` `[move name]`\n" +
                        "   - `move name` → can be multi-word, __**must match exactly**__\n" +
                        "   - not all games have move names\n" +
                        "   - `jin scourge`, `sol fafnir` etc."
            )

            mandatoryField(
                name = "",
                value = errorDescription,
                inline = false,
            )

            footer {
                text = "Got something to say, nerd? Use `/feedback`"
                icon = URL_IMG_FIGHTING_NERD
            }
        }
        val leftOverBuilder: EmbedBuilder.() -> Unit ={
            title = "ERROR"
            color = Color(RED)
            description = errorDescription
        }

        return BotOutput.MutableEmbedBuilder(
            primaryBuilder = primaryBuilder,
            autoEditBuilder = leftOverBuilder,
        )
    }

    private fun createQueryErrorEmbed(
        invalidQueryError: BotError.InvalidQuery,
    ): BotOutput.MutableEmbedBuilder {
        val errorText = invalidQueryError
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH)
        val errorDescription = createErrorDescription()

        val primaryBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)
            description = errorDescription

            description = errorText

            mandatoryField(
                name = "Command usage ⚙️".uppercase(),
                value = "- **tag** → `@FightingNerdBot` `[command]` `[query]`\n" +
                        "   - frame data (`fd`) is the default command; `@FightingNerdBot jun df1` works\n" +
                        "- **slash** → `/command`\n" +
                        "- use `/help` to see available commands"
            )

            mandatoryField(
                name = "",
                value = errorDescription,
                inline = false,
            )

            footer {
                text = "Got something to say, nerd? Use `/feedback`"
                icon = URL_IMG_FIGHTING_NERD
            }
        }

        val leftOverBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)
            description = errorDescription
        }

        return BotOutput.MutableEmbedBuilder(
            primaryBuilder = primaryBuilder,
            autoEditBuilder = leftOverBuilder,
        )
    }

    private fun createCommandErrorEmbed(
        error: BotError.InvalidCommand,
    ): BotOutput.MutableEmbedBuilder {
        val errorText = error
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH) + "\n Was **${error.command}** supposed to be a part of a character name?"
        val errorDescription = createErrorDescription()

        val primaryBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)
            description = errorText

            mandatoryField(
                name = "SYNTAX:",
                value = "If `${error.command}` was the start of a character name, **character names must be one word**\n" +
                        "- see game specific `/alias` (like `aliasTK` or `aliasBB`) for the full list",
                inline = false,
            )

            mandatoryField(
                name = "Command usage ⚙️".uppercase(),
                value = "- **tag** → `@FightingNerdBot` `[command]` `[query]`\n" +
                        "   - frame data (`fd`) is the default command; `@FightingNerdBot jun df1` works\n" +
                        "- **slash** → `/command`\n" +
                        "- use `/help` to see available commands"
            )

            mandatoryField(
                name = "",
                value = errorDescription,
                inline = false,
            )

            footer {
                text = "Got something to say, nerd? Use `/feedback`"
                icon = URL_IMG_FIGHTING_NERD
            }
        }
        val leftOverBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)
            description = errorDescription
        }

        return BotOutput.MutableEmbedBuilder(
            primaryBuilder = primaryBuilder,
            autoEditBuilder = leftOverBuilder,
        )
    }

    private fun createErrorDescription(): String {
        val text = "↓↓↓ **CLICK ONE OF THESE** ↓↓↓\n" +
                "- ${mention(Command.Fd)}\n" +
                "- ${mention(Command.Alias)}\n" +
                "- ${mention(Command.Help)} | ${mention(Command.Examples)} | ${mention(Command.Commands)}"
        return text
    }

    private fun mention(command: Command): String {
        val name = command.name.lowercase()
        val commandId = commandRegistry[name]
        val rendered = if (commandId != null) "</$name:${commandId.value}>" else "/$name"
        return rendered
    }
}


private const val RED = 0x00FF0000