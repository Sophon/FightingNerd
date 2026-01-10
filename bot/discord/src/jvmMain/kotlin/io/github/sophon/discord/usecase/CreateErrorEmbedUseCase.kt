package io.github.sophon.discord.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.BotError
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.util.mandatoryField

internal class CreateErrorEmbedUseCase {
    fun invoke(error: BotError): EmbedBuilder.() -> Unit {
        return when (error) {
            is BotError.UnknownCharacter,
            is BotError.UnknownMove,
                -> {
                createSyntaxErrorEmbed(error)
            }

            is BotError.InvalidQuery -> {
                createCommandSyntaxErrorEmbed(error)
            }

            else -> {
                createGenericError(error)
            }
        }
    }

    private fun createGenericError(error: BotError): EmbedBuilder.() -> Unit = {
        title = "ERROR"
        color = Color(RED)
        description = error.toString().truncate(EMBED_MAX_LENGTH)
    }

    private fun createSyntaxErrorEmbed(
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
                    "   - frame data (`fd`) is the default command@; `@FightingNerdBot jun df1` works\n" +
                    "- **slash** → `/command`\n" +
                    "- use `/help` to see available commands"
        )

        footer {
            text = "Got something to say, nerd? Use `/feedback`"
            icon = URL_IMG_FIGHTING_NERD
        }
    }
}


private const val RED = 0x00FF0000