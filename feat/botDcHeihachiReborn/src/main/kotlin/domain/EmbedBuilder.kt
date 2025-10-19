package domain

import BotError
import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import domain.serviceRegistry.ServiceInfo
import model.GlossaryItem
import model.Move
import util.field
import util.replaceItalic
import util.replaceUnderline

class EmbedBuilder {

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


    private fun GlossaryItem.format(): GlossaryItem {
        return this.copy(
            definition = this.definition.replaceUnderline(),
            jpTranslation = this.jpTranslation.map { it.replaceItalic() }
        )
    }
}

private const val GREEN = 0x00FF00
private const val ORANGE = 0x00FF6400