package io.github.sophon.discord.feat.dreamCancel

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.feature.FeatureInfo
import io.github.sophon.core.util.getGame
import io.github.sophon.core.wiki.domain.model.Move
import io.github.sophon.discord.util.featureFooter
import io.github.sophon.discord.util.mandatoryField
import io.github.sophon.discord.util.optionalField

internal fun dreamCancelMoveEmbed(
    gameId: String,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = move.input
    url = move.urls.wikiUrl
    description = if (move.name.isNullOrBlank()) {
        "**${move.charName}**"
    } else {
        "**${move.charName}**: ${move.name.orEmpty()}"
    }
    color = Color(BLUE)

    val images = move.urls.hitboxImageList.takeIf { it.isNotEmpty() }
        ?: emptyList()

    images
        .takeIf { it.size == 1 }
        ?.let { image = it.first() }

    gameId.getGame()?.iconUrl?.let {
        thumbnail { url = it }
    }

    mandatoryField(name = "Startup", value = move.startup)
    mandatoryField(name = "Hit", value = move.onHit)
    mandatoryField(name = "Block", value = move.onBlock)
    mandatoryField(name = "Active", value = move.active)
    mandatoryField(name = "Guard", value = move.guard)
    mandatoryField(name = "Recovery", value = move.recovery)

    optionalField(name = "Damage", value = move.damage)
    optionalField(name = "Invul", value = move.invulnerability)
    optionalField(name = "Stun", value = move.koF15Properties?.stun)
    optionalField(name = "Rev dmg", value = move.cotwProperties?.revDamage)

    featureFooter(featureInfo)
}

private const val BLUE = 0x009AB3F6