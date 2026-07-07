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

    character.roa2Properties?.weight?.let { mandatoryField(name = "Weight", value = it) }
    character.roa2Properties?.hitstunGravity?.let { mandatoryField(name = "Hitstun Gravity", value = it) }
    character.roa2Properties?.fallSpeedMax?.let { mandatoryField(name = "Max Fall Spd", value = it) }

    character.roa2Properties?.dashSpeed?.let { mandatoryField(name = "Max Fall Spd", value = it) }
    character.roa2Properties?.dashFrames?.let { mandatoryField(name = "Dash Frames", value = it) }
//    character.roa2Properties?.dashAcceleration?.let { mandatoryField(name = "Dash Acc", value = it) } //useful?
    character.roa2Properties?.frictionGround?.let { mandatoryField(name = "Ground friction", value = it) }

    character.roa2Properties?.jumpSpeedHorizontalMax?.let { mandatoryField(name = "Horizontal Jump Spd", value = it) }
    character.roa2Properties?.airSpeedHorizontalMax?.let { mandatoryField(name = "Horizontal Air Spd", value = it) }
    character.roa2Properties?.airAcceleration?.let { mandatoryField(name = "Air Acc", value = it) }

    character.roa2Properties?.shortHopSpeed?.let { mandatoryField(name = "Short Hop Height", value = it) }
    character.roa2Properties?.fullHopSpeed?.let { mandatoryField(name = "Full Hop Height", value = it) }
    character.roa2Properties?.doubleJumpSpeed?.let { mandatoryField(name = "Double Jump Spd", value = it) }



    featureFooter(featureInfo)
}

private const val TEAL = 0x002893F0