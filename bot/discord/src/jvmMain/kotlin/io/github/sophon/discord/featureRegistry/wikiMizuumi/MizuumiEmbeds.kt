package io.github.sophon.discord.featureRegistry.wikiMizuumi

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.wiki.domain.model.Move
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


private const val TEAL = 0x0007A9F5