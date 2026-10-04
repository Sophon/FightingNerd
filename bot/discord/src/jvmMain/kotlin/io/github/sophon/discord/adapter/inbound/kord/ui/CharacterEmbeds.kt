package io.github.sophon.discord.adapter.inbound.kord.ui

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.invisibleChar
import io.github.sophon.core.util.toColumns
import io.github.sophon.discord.app.domain.model.BotResponse

internal fun characterEmbed(
    character: BotResponse.CharacterResponse,
): EmbedBuilder.() -> Unit = {
    color = Color(character.dataSource.color)
    title = character.displayName
    url = character.url
    if (character.aliasList.isNotEmpty()) {
        description = "Aliases: ${character.aliasList.joinToString(", ")}"
    }

    character.propertyList.forEach { field ->
        mandatoryField(name = field.title, value = field.value)
    }

    featureFooter(dataSource = character.dataSource)
}

internal fun aliasEmbed(
    characterList: List<BotResponse.CharacterResponse>,
): EmbedBuilder.() -> Unit = {
    val aliasList = characterList
        .filter { it.aliasList.isNotEmpty() }
        .sortedBy { it.displayName }
        .mapIndexed { index, character ->
            val aliases = character.aliasList.joinToString(", ")
            "${index + 1}. **${character.displayName}** → $aliases"
        }

    if (aliasList.isEmpty()) {
        mandatoryField(
            name = "🥸 Character aliases",
            value = "No character aliases found, everyone's honest.",
            inline = false,
        )
    } else {
        mandatoryField(
            name = "🥸 Character aliases",
            value = invisibleChar,
            inline = false,
        )

        aliasList
            .toColumns()
            .forEach { column ->
                val text = column.joinToString("\n")
                mandatoryField(
                    name = "",
                    value = text,
                )
            }
    }

    characterList
        .firstOrNull()
        ?.let { character ->
            color = Color(character.dataSource.color)
            featureFooter(character.dataSource)
        }
}

internal fun aliasGamePromptEmbed(
    gameList: List<String>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    val numberedGames = gameList
        .mapIndexed { index, game -> "${index + 1}. **$game**" }
        .joinToString("\n")

    mandatoryField(
        name = "🥸 Character aliases",
        value = "Please select the game from the options below.",
        inline = false,
    )

    mandatoryField(
        name = "",
        value = numberedGames,
        inline = false,
    )

    featureFooter(featureInfo)
}
