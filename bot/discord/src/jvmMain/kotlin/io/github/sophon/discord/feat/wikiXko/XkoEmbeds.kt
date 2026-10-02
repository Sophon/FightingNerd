package io.github.sophon.discord.feat.wikiXko

import dev.kord.common.Color
import dev.kord.rest.builder.message.EmbedBuilder
import io.github.sophon.core.featureConfig.model.FeatureInfo
import io.github.sophon.core.util.orDash
import io.github.sophon.core.wiki.model.Character
import io.github.sophon.core.wiki.model.Move
import io.github.sophon.discord.adapter.inbound.kord.embedImage
import io.github.sophon.discord.adapter.inbound.kord.featureFooter
import io.github.sophon.discord.adapter.inbound.kord.mandatoryField
import io.github.sophon.discord.adapter.inbound.kord.moveEmbedDescription
import io.github.sophon.discord.adapter.inbound.kord.optionalField

internal fun xkoMoveEmbed(
    character: Character,
    move: Move,
    featureInfo: FeatureInfo,
): EmbedBuilder.() -> Unit = {
    title = "${move.characterId}: ${move.input.uppercase()}"
    moveEmbedDescription(character, move)

    embedImage(move.urls.hitboxImageList)

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