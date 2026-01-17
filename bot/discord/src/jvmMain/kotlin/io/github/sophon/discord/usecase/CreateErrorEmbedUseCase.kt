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
                syntaxErrorEmbed(error) to examplesButton()
            }

            is BotError.InvalidQuery -> {
                createCommandSyntaxErrorEmbed(error) to commandsButton()
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
            .truncate(EMBED_MAX_LENGTH)

        mandatoryField(
            name = "",
            value = "THE **CHARACTER** NAME AND MOVE **INPUT** MUST HAVE **NO SPACES**. MOVE **NAME** CAN BE MULTI-WORD BUT MUST MATCH EXACTLY.\n\n" +
                    "1. **`@bot`** → `@FightingNerdBot` `[character name]` `[move]`\n" +
                    "2. **`/fd`** → `fd character: [character name] move: [move]`\n\n" +
                    "- multi-word character name →`/alias` to see the available aliases\n" +
                    "- stances or heat are followed by a dot: `nina h.b1+2` or `ak bad.3h.2`\n" +
                    "- check `/heat`, `/homing`, `/stance`, `/pc` etc\n" +
                    "- if stuck, check the wiki to see how it's written"
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

    private fun examplesButton(): BotOutput.ButtonSet {
        return BotOutput.ButtonSet(
            buttonList = listOf(
                BotOutput.EmbedButton(
                    label = "EXAMPLES",
                    action = BotOutput.EmbedButton.Action.Query("examples")
                )
            ),
            duration = EMBED_BUTTON_DURATION_INF.seconds,
        )
    }

    private fun commandsButton(): BotOutput.ButtonSet {
        return BotOutput.ButtonSet(
            buttonList = listOf(
                BotOutput.EmbedButton(
                    label = "COMMANDS",
                    action = BotOutput.EmbedButton.Action.Query("commands")
                )
            ),
            duration = EMBED_BUTTON_DURATION_INF.seconds,
        )
    }

    private fun helpButton(): BotOutput.ButtonSet {
        return BotOutput.ButtonSet(
            buttonList = listOf(
                BotOutput.EmbedButton(
                    label = "HELP",
                    action = BotOutput.EmbedButton.Action.Query("help")
                )
            ),
            duration = EMBED_BUTTON_DURATION_INF.seconds,
        )
    }
}


private const val RED = 0x00FF0000