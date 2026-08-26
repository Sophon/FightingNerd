package io.github.sophon.discord.feat.wikiDragDown

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.capitalize
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField
import io.github.sophon.wikidragdown.integration.model.Roa2Properties

internal fun dragDownMoveEmbed(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = move.input.formatTitle(move.roa2Properties?.mode)
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
        value = move.notes.joinToString(";") { "- $it"},
        inline = false,
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

    (character.gameProperties as? Roa2Properties)?.apply {
        weight?.let { mandatoryField(name = "Weight", value = it) }
        hitstunGravity?.let { mandatoryField(name = "Hitstun Gravity", value = it) }
        fallSpeedMax?.let { mandatoryField(name = "Max Fall Spd", value = it) }

        dashSpeed?.let { mandatoryField(name = "Max Fall Spd", value = it) }
        dashFrames?.let { mandatoryField(name = "Dash Frames", value = it) }
        frictionGround?.let { mandatoryField(name = "Ground friction", value = it) }

        jumpSpeedHorizontalMax?.let { mandatoryField(name = "Horizontal Jump Spd", value = it) }
        airSpeedHorizontalMax?.let { mandatoryField(name = "Horizontal Air Spd", value = it) }
        airAcceleration?.let { mandatoryField(name = "Air Acc", value = it) }

        shortHopSpeed?.let { mandatoryField(name = "Short Hop Height", value = it) }
        fullHopSpeed?.let { mandatoryField(name = "Full Hop Height", value = it) }
        doubleJumpSpeed?.let { mandatoryField(name = "Double Jump Spd", value = it) }
    }
    

    featureFooter(featureInfo)
}

private fun String.formatTitle(mode: String?): String {
    if (mode.isNullOrEmpty() || endsWith(mode).not()) return this

    val base = removeSuffix(mode)
    val formatted = "$base ${mode.capitalize()}"
    return formatted
}


private const val TEAL = 0x002893F0