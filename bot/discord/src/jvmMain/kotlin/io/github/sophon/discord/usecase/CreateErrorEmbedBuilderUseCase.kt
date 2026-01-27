package io.github.sophon.discord.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_BUTTON_DURATION_INF
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.domain.BotOutput
import io.github.sophon.discord.util.mandatoryField
import kotlin.time.Duration.Companion.seconds

internal class CreateErrorEmbedBuilderUseCase {
    fun invoke(error: BotError): Pair<BotOutput.EditableEmbedBuilder, BotOutput.ButtonSet?> {
        return when (error) {
            is BotError.UnknownCharacter,
            is BotError.UnknownMove,
                -> {
                syntaxErrorEmbed(error) to BotOutput.ButtonSet(
                    buttonList = listOf(examplesButton(), commandsButton()),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                )
            }

            is BotError.InvalidQuery -> {
                createCommandSyntaxErrorEmbed(error) to BotOutput.ButtonSet(
                    buttonList = listOf(commandsButton(), helpButton()),
                    duration = EMBED_BUTTON_DURATION_INF.seconds,
                )
            }

            else -> {
                createGenericError(error) to null
            }
        }
    }

    private fun createGenericError(error: BotError): BotOutput.EditableEmbedBuilder {
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

        return BotOutput.EditableEmbedBuilder(primaryBuilder, leftOverBuilder)
    }

    private fun syntaxErrorEmbed(
        unknownCharacterError: BotError,
    ): BotOutput.EditableEmbedBuilder {
        val errorDescription = unknownCharacterError
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH)

        val primaryBuilder: EmbedBuilder.() -> Unit ={
            title = "ERROR"
            color = Color(RED)

            description = errorDescription

            mandatoryField(
                name = "",
                value = "1. `[character name]` `[move input]`\n" +
                        "   - `character name` →  __**NO SPACES**__\n" +
                        "   - `move input` → __**NO SPACES**__\n" +
                        "   - `lee df1`, `ak h.bad.32`, `ling bt.4`, `ken 236pp` etc.\n\n" +
                        "2. `[character name]` `[move name]`\n" +
                        "   - `move name` → can be multi-word, __**must match exactly**__\n" +
                        "   - not all games have move names\n" +
                        "   - `jin scourge`, `sol fafnir` etc."
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

        return BotOutput.EditableEmbedBuilder(primaryBuilder, leftOverBuilder)
    }

    private fun createCommandSyntaxErrorEmbed(
        invalidQueryError: BotError.InvalidQuery,
    ): BotOutput.EditableEmbedBuilder {
        val errorDescription = invalidQueryError
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH)

        val primaryBuilder: EmbedBuilder.() -> Unit = {
            title = "ERROR"
            color = Color(RED)

            description = errorDescription

            mandatoryField(
                name = "Command usage ⚙️".uppercase(),
                value = "- **tag** → `@FightingNerdBot` `[command]` `[query]`\n" +
                        "   - frame data (`fd`) is the default command; `@FightingNerdBot jun df1` works\n" +
                        "- **slash** → `/command`\n" +
                        "- use `/help` to see available commands"
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

        return BotOutput.EditableEmbedBuilder(primaryBuilder, leftOverBuilder)
    }

    private fun examplesButton(): BotOutput.EmbedButton {
        return BotOutput.EmbedButton(
            label = "EXAMPLES",
            action = BotOutput.EmbedButton.Action.Query("examples")
        )
    }

    private fun commandsButton(): BotOutput.EmbedButton {
        return BotOutput.EmbedButton(
            label = "COMMANDS",
            action = BotOutput.EmbedButton.Action.Query("commands")
        )
    }

    private fun helpButton(): BotOutput.EmbedButton {
        return BotOutput.EmbedButton(
            label = "HELP",
            action = BotOutput.EmbedButton.Action.Query("help")
        )
    }
}


private const val RED = 0x00FF0000