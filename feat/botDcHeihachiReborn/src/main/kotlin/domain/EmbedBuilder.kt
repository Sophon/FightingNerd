package domain

import BotError
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import domain.serviceRegistry.ServiceInfo
import model.GlossaryItem
import model.Move

class EmbedBuilder {
    fun moveEmbed(
        move: Move,
        info: ServiceInfo,
    ): EmbedBuilder.() -> Unit = {
        title = move.characterName //TODO: clickable
        description = "${move.id} - ${move.name}" //TODO: clickable
        color = Color(GREEN)

        field(name = "Startup", value = move.startup,)
        field(name = "OH", value = move.onHit,)
        field(name = "OB", value = move.onBlock,)
        field(name = "CH", value = move.onCH ?: move.onHit,)
        field(name = "Level", value = move.level,)
        if (move.recoveryOnWhiff.isNullOrEmpty().not()) {
            field(name = "Recovery", value = move.recoveryOnWhiff)
        }

        field(name = "Damage", value = move.damage.orEmpty(),)

        field(
            name = "📝 Notes",
            value = move.notes.joinToString(separator = "") { "* $it\n" },
            inline = false,
        )

        //TODO: feedback command
        footer {
            text = info.name
            icon = info.iconUrl
        }
    }

    fun glossaryEmbed(
        item: GlossaryItem,
        info: ServiceInfo,
    ): EmbedBuilder.() -> Unit = {
        val formattedItem = item.format()
        title = formattedItem.term
        color = Color(ORANGE)

        field(name = "", value = formattedItem.definition.replaceUnderline(), inline = false)

        val japaneseValueString = formattedItem.jpTranslation
            .joinToString(separator = "") { "* $it\n" }
        field(name = "🇯🇵", value = japaneseValueString, inline = false)

        footer {
            text = info.name
            icon = info.iconUrl
        }
    }

    fun errorEmbed(error: BotError): EmbedBuilder.() -> Unit = {
        title = "Error"
        description = error.toString()
    }


    private fun EmbedBuilder.field(
        name: String,
        value: String?,
        inline: Boolean = true,
    ) {
        field {
            this.name = name
            this.value = value.orEmpty()
            this.inline = inline
        }
    }

    private fun GlossaryItem.format(): GlossaryItem {
        return this.copy(
            definition = this.definition.replaceUnderline(),
            jpTranslation = this.jpTranslation.map { it.replaceItalic() }
        )
    }

    private fun String.replaceItalic(): String {
        return this
            .replace("<em>", "*")
            .replace("</em>", "*")
    }

    /**
     * Replaces HTML tags with Markdown.
     *
     * Examples:
     * - `!<'block'>` → `__block__`
     * - `!<'whiff punish','whiff'>` → `__whiff__`
     *
     * Takes the last comma-separated value if multiple are present.
     */
    private fun String.replaceUnderline(): String {
        return this.replace(Regex("!<'([^']+)'(?:,'[^']*')*>")) { matchResult ->
            val content = matchResult.groupValues[1]
            val words = content.split("','")
            val lastWord = words.last()
            "**__${lastWord}__**"
        }
    }
}

private const val GREEN = 0x00FF00
private const val ORANGE = 0x00FF6400