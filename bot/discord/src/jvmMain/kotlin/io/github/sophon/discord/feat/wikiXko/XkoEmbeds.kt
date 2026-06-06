package io.github.sophon.discord.feat.wikiXko

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal fun xkoMoveEmbed(
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "${move.charName}: ${move.input.uppercase()}"
    description = if (move.name.isNullOrBlank()) {
        "**${move.charName}**"
    } else {
        "**${move.charName}**: ${move.name.orEmpty()}"
    }

    val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
        ?: emptyList()
    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }

    color = Color(GREEN)

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Block", value = move.onBlock)
    mandatoryField(name = "Guard", value = move.guard)
    mandatoryField(name = "Active", value = move.active.orDash())

    optionalField(name = "Recovery", value = move.recovery)
    optionalField(name = "Damage", value = move.damage)

    featureFooter(featureInfo)
}


private const val GREEN = 0xCDF564