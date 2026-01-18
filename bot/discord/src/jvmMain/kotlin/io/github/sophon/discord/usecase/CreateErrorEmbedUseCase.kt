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

internal class CreateErrorEmbedUseCase {
    fun invoke(error: BotError): Pair<EmbedBuilder.() -> Unit, BotOutput.ButtonSet?> {
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

    private fun createGenericError(error: BotError): EmbedBuilder.() -> Unit = {
        title = "ERROR"
        color = Color(RED)
        description = error.toString().truncate(EMBED_MAX_LENGTH)
    }

    private fun syntaxErrorEmbed(
        unknownCharacterError: BotError,
    ): EmbedBuilder.() -> Unit = {
        title = "ERROR"
        color = Color(RED)

        description = unknownCharacterError
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH)

        mandatoryField(
            name = "",
            value = "The **character name** and move **input** must have __**NO SPACES**__.\n" +
                    "**Move name** can be multi-word, but must match exactly.\n\n" +
                    "1. **`@bot`** → `@FightingNerdBot` `[character name]` `[move]`\n" +
                    "2. **`/fd`** → `fd character: [character name] move: [move]`\n"
        )

        footer {
            text = "Got something to say, nerd? Use `/feedback`"
            icon = URL_IMG_FIGHTING_NERD
        }
    }

    private fun createCommandSyntaxErrorEmbed(
        invalidQueryError: BotError.InvalidQuery,
    ): EmbedBuilder.() -> Unit = {
        title = "ERROR"
        color = Color(RED)

        description = invalidQueryError
            .toString()
            .let { "**$it**" }
            .truncate(EMBED_MAX_LENGTH)

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