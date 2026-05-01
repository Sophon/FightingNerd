package io.github.sophon.discord.feat.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.chunkByNewLines
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.domain.model.Character
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.EMBED_MAX_LENGTH
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal fun mizuumiMoveEmbed(
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = move.input
    url = move.urls.wikiUrl
    description = when {
        move.charName.isBlank() -> "Move data"
        move.name.isNullOrBlank() -> "**${move.charName}**"
        else -> "**${move.charName}**: ${move.name}"
    }
    color = Color(TEAL)
    move.urls.characterImage?.let { thumbnail { url = it } }

    val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
        ?: emptyList()

    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Active", value = move.active)
    optionalField(name = "OH", value = move.onHit)
    mandatoryField(name = "Block", value = move.onBlock)
    mandatoryField(name = "Cancel", value = move.cancel)
    mandatoryField(name = "Guard", value = move.guard)
    mandatoryField(name = "Recovery", value = move.recovery)

    optionalField(name = "Damage", value = move.damage, escapeAsterisks = true)
    optionalField(name = "Invul", value = move.invulnerability)

    move.mbProperties?.apply {
        optionalField(name = "Attribute", value = attribute)
        optionalField(name = "Property", value = property)
        optionalField(name = "Cost", value = move.mbProperties?.cost)
    }

    move.uni2Properties?.apply {
        optionalField(name = "Attribute", value = attribute)
        optionalField(name = "Property", value = property)
        optionalField(name = "Cost", value = move.mbProperties?.cost)
        optionalField(name = "Ass advantage", value = assaultAdv)
    }

    move.vsavProperties?.apply {
        mandatoryField(name = "W-Dmg", value = whiteDmg)
        mandatoryField(name = "Renda", value = renda)
        mandatoryField(name = "Meter", value = meter)
        mandatoryField(name = "Reaction", value = reaction)
        optionalField(name = "Curse", value = curseTime)
    }

    featureFooter(featureInfo)
}

internal fun mizuumiMoveListEmbed(
    featureInfo: FeatureInfo,
    category: String,
    moveList: List<Move>,
): EmbedBuilder.() -> Unit = {
    color = Color(TEAL)

    val text = moveList
        .mapIndexed { index, move ->
            "${index + 1}. **${move.input}** (${move.invulnerability})"
        }
        .joinToString("\n")

    text
        .chunkByNewLines(delimiter = "\n", maxLength = EMBED_MAX_LENGTH)
        .forEachIndexed { index, data ->
            mandatoryField(
                name = if (index == 0) "$category moves" else "",
                value = data,
            )
        }

    featureFooter(featureInfo)
}

internal fun mizuumiCharacterEmbed(
    character: Character,
    fastestMoveList: List<Move>,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = character.displayName
    url = character.wikiUrl
    color = Color(TEAL)
    character.images?.iconUrl?.let { iconUrl ->
        thumbnail { url = iconUrl }
    }

    val moves = fastestMoveList.joinToString(", ") { it.input }
    val startup = fastestMoveList.first().startup.orDash()
    mandatoryField(
        name = "Fastest normal",
        value = "${startup}f: $moves"
    )
    mandatoryField(name = "HP", character.hp)
    mandatoryField(
        name = "Umo",
        value = if (character.umo.size == 1) {
            character.umo.toString()
        } else {
            character.umo.joinToString {
                "- $it\n"
            }
        },
    )

    character.uni2Properties?.apply {
        optionalField(name = "Jump", value = "**$jumpStartup** ($jumpDuration)")

        val walkValue = buildString {
            bWalkSpeed?.let { append("← **$it**") }
            fWalkSpeed?.let { append(" → **$it** ") }
        }
        optionalField(name = "Walk", value = walkValue)

        val bDashValue = buildString {
            bDashStartup?.let { append("**${it}f**") }
            bDashDuration?.let { append(" - dur: $it") }
            bDashDistance?.let { append(" dist: $it\n") }
            append("Inv: **$bDashFullInvulStart - $bDashFullInvulEnd**")
            append(" Thr: **$bDashThrowInvulStart - $bDashThrowInvulEnd**")
        }
        optionalField(name = "bDash", value = bDashValue)

        optionalField(name = "Vorpal", value = character.uni2Properties?.vorpalTrait)
    }

    optionalField(
        name = "Trait",
        value = character.uni2Properties?.trait,
        inline = false,
    )

    featureFooter(featureInfo)
}


private const val TEAL = 0x0007A9F5