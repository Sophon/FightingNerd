package io.github.sophon.discord.feat.bot.usecase

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.architecture.ExcludeFromCoverage
import io.github.sophon.core.util.truncate
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.URL_IMG_FIGHTING_NERD
import io.github.sophon.discord.feat.core.domain.CommandRegistry
import io.github.sophon.discord.feat.core.domain.model.BotError
import io.github.sophon.discord.feat.core.domain.model.Command
import io.github.sophon.discord.util.mandatoryField

@ExcludeFromCoverage("UI")
internal class CreateErrorEmbedBuilderUseCase(
    private val commandRegistry: CommandRegistry,
) {
    fun invoke(error: BotError): EmbedBuilder.() -> Unit = {
        title = "ERROR"
        color = Color(RED)
        description = "**$error**".truncate(EMBED_MAX_LENGTH)

        mandatoryField(
            name = "↓↓↓ **CLICK THESE** ↓↓↓",
            value = "Slash commands have **auto complete**.",
            inline = false,
        )

        mandatoryField(
            name = "Frame Data",
            value = commandRegistry.mention(Command.Fd),
        )

        mandatoryField(
            name = "Character Names",
            value = commandRegistry.mention(Command.Alias),
        )

        mandatoryField(
            name = "Other",
            value = "${commandRegistry.mention(Command.Help)} | ${commandRegistry.mention(Command.Examples)} | ${commandRegistry.mention(Command.Commands)}",
            inline = false,
        )

        footer {
            text = "Got something to say, nerd? Use `/feedback`"
            icon = URL_IMG_FIGHTING_NERD
        }

    }
}


private const val RED = 0x00FF0000
