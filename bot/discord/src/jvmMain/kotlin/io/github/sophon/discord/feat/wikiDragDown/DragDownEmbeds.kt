package io.github.sophon.discord.feat.wikiDragDown

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal fun dragDownMoveEmbed(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = move.input
    url = move.urls.wikiUrl
    description = "**${character.displayName}**"
    color = Color(TEAL)
    character.images?.iconUrl?.let { thumbnail { url = it } }

    val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
        ?: emptyList()
    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Active", value = move.active)
    mandatoryField(name = "Recovery", value = move.recovery)
    mandatoryField(name = "Landing", value = move.roa2Properties?.landingLag)
    mandatoryField(name = "IASA", value = move.roa2Properties?.iasa)
    mandatoryField(name = "Ledge", value = move.roa2Properties?.ledgeGrabFrame)

    optionalField(name = "Cancel", move.cancel)
    move.roa2Properties?.uniqueField?.let { uniqueFields ->
        val value = uniqueFields.joinToString(";") { "- $it"}
        optionalField(name = "Unique", value = value)
    }

    optionalField(
        name = "Notes",
        value = move.notes.joinToString(";") { "- $it"}
    )

    featureFooter(featureInfo)
}

internal fun dragDownCharacterEmbed(
    character: Character,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = character.displayName
    url = character.wikiUrl
    color = Color(TEAL)
    character.images?.iconUrl?.let { image = it }

    //TODO: data

    featureFooter(featureInfo)
}

private const val TEAL = 0x002893F0